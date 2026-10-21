package com.francesco.chalettime.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.francesco.chalettime.data.repository.*;
import com.francesco.chalettime.model.*;
import com.francesco.chalettime.ui.common.BaseViewModel;
import java.util.*;

public class AdminViewModel extends BaseViewModel {
    private final EmployeeRepository employees;
    private final AuthRepository auth;
    private final MutableLiveData<Integer> selectedMonth = new MutableLiveData<>(-1);
    private final MutableLiveData<List<Employee>> employeeList = new MutableLiveData<>(Collections.emptyList());
    private List<Employee> all = new ArrayList<>();
    private String query = "";
    private int generation;
    public AdminViewModel() { this(new EmployeeRepositoryImpl(), new AuthRepositoryImpl()); }
    public AdminViewModel(EmployeeRepository employees, AuthRepository auth) { this.employees = employees; this.auth = auth; }
    public LiveData<Integer> getSelectedMonth() { return selectedMonth; }
    public LiveData<List<Employee>> getEmployeeList() { return employeeList; }
    public String getQuery() { return query; }
    public void logout() { auth.signOut(); }
    public void selectMonth(int month) {
        if (month < 1 || month > 12) return;
        selectedMonth.setValue(month);
        final int request = ++generation;
        loading.setValue(true);
        all = new ArrayList<>(); publish();
        employees.fetchAllUsers(new EmployeeRepository.FetchUsersCallback() {
            @Override public void onSuccess(List<Employee> users) {
                if (!current(request)) return;
                if (users.isEmpty()) { loading.setValue(false); publish(); return; }
                List<Employee> result = new ArrayList<>(users);
                int[] remaining = {users.size()};
                int year = Calendar.getInstance().get(Calendar.YEAR);
                for (Employee user : users) {
                    employees.fetchUserWorkLogs(user.getUserId(), year, month, new EmployeeRepository.FetchWorkLogsCallback() {
                        private void complete(double hours) {
                            if (!current(request)) return;
                            user.setHours(String.format(Locale.ITALY, "%.1f ore", hours));
                            if (--remaining[0] == 0) {
                                all = result; publish(); loading.setValue(false);
                            }
                        }
                        @Override public void onSuccess(UserWorkLogs data) {
                            double hours = 0;
                            if (data != null && data.getLogs() != null) for (WorkLog log : data.getLogs()) {
                                if (matches(log.getDate(), year, month)) hours += log.getHours();
                            }
                            complete(hours);
                        }
                        @Override public void onFailure(String error) { if (current(request)) { message(error); complete(0); } }
                    });
                }
            }
            @Override public void onFailure(String error) { if (current(request)) { loading.setValue(false); message(error); } }
        });
    }
    private boolean current(int request) { return !cleared && request == generation; }
    static boolean matches(String date, int year, int month) {
        if (date == null) return false;
        String[] parts = date.split("/");
        if (parts.length != 3) return false;
        try { return Integer.parseInt(parts[1]) == month && Integer.parseInt(parts[2]) == year; }
        catch (NumberFormatException e) { return false; }
    }
    public void filter(String query) { this.query = query; publish(); }
    private void publish() {
        List<Employee> filtered = new ArrayList<>();
        for (Employee employee : all) if (employee.getName().toLowerCase(Locale.ITALY).contains(query.toLowerCase(Locale.ITALY))) filtered.add(employee);
        employeeList.setValue(Collections.unmodifiableList(filtered));
    }
}
