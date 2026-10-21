package com.francesco.chalettime.ui.home.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.ViewModelStore;
import com.francesco.chalettime.data.repository.*;
import com.francesco.chalettime.model.*;
import com.francesco.chalettime.ui.welcome.viewmodel.AuthViewModel;
import org.junit.Rule;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class MvvmTest {
    @Rule public InstantTaskExecutorRule mainThread = new InstantTaskExecutorRule();

    static class FakeAuth implements AuthRepository {
        String id = "employee-1";
        AuthCallback signIn, create, profile;
        User savedUser;
        @Override public String getCurrentUserId() { return id; }
        @Override public void signOut() { id = null; }
        @Override public void signInWithEmailAndPassword(String email, String password, AuthCallback callback) { signIn = callback; }
        @Override public void createUserWithEmailAndPassword(String email, String password, AuthCallback callback) { create = callback; }
        @Override public void sendPasswordResetEmail(String email, AuthCallback callback) { callback.onSuccess(); }
        @Override public void saveUserToFirestore(String id, User user, AuthCallback callback) { savedUser = user; profile = callback; }
    }
    static class FakeLogs implements UserWorkLogRepository {
        int writes;
        WorkLog saved;
        SaveWorkLogCallback pending;
        @Override public void saveWorkLog(String id, WorkLog log, SaveWorkLogCallback callback) { writes++; saved = log; pending = callback; }
    }
    @Test public void registrationWaitsForProfileBeforeNavigation() {
        FakeAuth auth = new FakeAuth();
        AuthViewModel model = new AuthViewModel(auth);
        model.register("user@example.com", "password", "Ada", "Rossi", "01/01/1990", "Femmina");
        assertTrue(model.getLoading().getValue());
        auth.create.onSuccess();
        assertNull(model.getAuthenticated().getValue());
        assertEquals("user", auth.savedUser.getRole());
        auth.profile.onSuccess();
        assertFalse(model.getLoading().getValue());
        assertTrue(model.getAuthenticated().getValue().consume());
        assertNull(model.getAuthenticated().getValue().consume());
    }
    @Test public void loginFailureLeavesNavigationUntouchedAndAllowsRetry() {
        FakeAuth auth = new FakeAuth();
        AuthViewModel model = new AuthViewModel(auth);
        model.login("user@example.com", "bad");
        auth.signIn.onFailure(new Exception("denied"));
        assertFalse(model.getLoading().getValue());
        assertNull(model.getAuthenticated().getValue());
        assertNotNull(model.getMessages().getValue().consume());
        model.login("user@example.com", "valid");
        auth.signIn.onSuccess();
        assertNotNull(model.getAuthenticated().getValue());
    }
    @Test public void invalidWorkHoursNeverReachTheRepository() {
        FakeLogs logs = new FakeLogs();
        HomeViewModel model = new HomeViewModel(new FakeAuth(), logs);
        model.save("", "8");
        model.save("31/02/2026", "8");
        model.save("01/10/2026", "NaN");
        model.save("01/10/2026", "8.25");
        model.save("01/10/2026", "17");
        assertEquals(0, logs.writes);
        assertFalse(model.getLoading().getValue());
    }
    @Test public void saveDoesNotDuplicateOrClearFormUntilSuccessful() {
        FakeLogs logs = new FakeLogs();
        HomeViewModel model = new HomeViewModel(new FakeAuth(), logs);
        model.save("01/10/2026", "8.5");
        model.save("01/10/2026", "8.5");
        assertEquals(1, logs.writes);
        assertEquals(8.5, logs.saved.getHours(), 0);
        logs.pending.onFailure("offline");
        assertNull(model.getSaved().getValue());
        assertFalse(model.getLoading().getValue());
        model.save("01/10/2026", "8.5");
        logs.pending.onSuccess("ok");
        assertTrue(model.getSaved().getValue().consume());
    }
    @Test public void unauthenticatedSaveDoesNotWrite() {
        FakeAuth auth = new FakeAuth(); auth.id = null;
        FakeLogs logs = new FakeLogs();
        new HomeViewModel(auth, logs).save("01/10/2026", "8");
        assertEquals(0, logs.writes);
    }
    static class FakeEmployees implements EmployeeRepository {
        final List<FetchUsersCallback> requests = new ArrayList<>();
        final List<FetchWorkLogsCallback> totals = new ArrayList<>();
        @Override public void fetchAllUsers(FetchUsersCallback callback) { requests.add(callback); }
        @Override public void fetchUserWorkLogs(String id, int year, int month, FetchWorkLogsCallback callback) { totals.add(callback); }
    }
    @Test public void previousMonthCannotOverwriteTheLatestSelection() {
        FakeEmployees repo = new FakeEmployees();
        AdminViewModel model = new AdminViewModel(repo, new FakeAuth());
        model.selectMonth(1);
        repo.requests.get(0).onSuccess(Collections.singletonList(new Employee("a", "Ada Rossi", "", "Femmina", "")));
        model.selectMonth(2);
        repo.requests.get(1).onSuccess(Collections.emptyList());
        repo.totals.get(0).onSuccess(new UserWorkLogs("a", Collections.emptyList()));
        assertEquals(Integer.valueOf(2), model.getSelectedMonth().getValue());
        assertTrue(model.getEmployeeList().getValue().isEmpty());
        assertFalse(model.getLoading().getValue());
    }
    @Test public void filteringSurvivesAsyncResultsAndMalformedDatesAreIgnored() {
        FakeEmployees repo = new FakeEmployees();
        AdminViewModel model = new AdminViewModel(repo, new FakeAuth());
        model.selectMonth(10); model.filter("ADA");
        repo.requests.get(0).onSuccess(Arrays.asList(new Employee("a", "Ada Rossi", "", "Femmina", ""), new Employee("b", "Mario Bianchi", "", "Maschio", "")));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        repo.totals.get(1).onSuccess(new UserWorkLogs("b", Collections.emptyList()));
        repo.totals.get(0).onSuccess(new UserWorkLogs("a", Arrays.asList(new WorkLog("a", "01/10/" + year, 8), new WorkLog("a", "bad/date/value", 9))));
        assertEquals(1, model.getEmployeeList().getValue().size());
        assertEquals("Ada Rossi", model.getEmployeeList().getValue().get(0).getName());
        assertEquals("8,0 ore", model.getEmployeeList().getValue().get(0).getHours());
    }
    @Test public void clearedViewModelIgnoresLateCallbacks() {
        FakeAuth auth = new FakeAuth();
        AuthViewModel model = new AuthViewModel(auth);
        ViewModelStore store = new ViewModelStore(); store.put("auth", model);
        model.login("a@b.it", "password"); store.clear(); auth.signIn.onSuccess();
        assertNull(model.getAuthenticated().getValue());
    }
    @Test public void mainRoleIsLoadedOnlyOnceAndPreservesTheRoleRule() {
        final UserDetailsRepository.FetchUserDetailsByIdCallback[] callback = {null};
        final int[] calls = {0};
        UserDetailsRepository users = new UserDetailsRepository() {
            @Override public void fetchUserDetails(String first, String last, FetchUserDetailsCallback result) { fail(); }
            @Override public void fetchUserDetailsById(String id, FetchUserDetailsByIdCallback result) { calls[0]++; callback[0] = result; }
        };
        MainViewModel model = new MainViewModel(new FakeAuth(), users);
        model.loadRole(); model.loadRole();
        assertEquals(1, calls[0]);
        callback[0].onSuccess(new User("Ada", "Rossi", "", "", "admin", ""));
        assertEquals("admin", model.getRole().getValue());
    }
}
