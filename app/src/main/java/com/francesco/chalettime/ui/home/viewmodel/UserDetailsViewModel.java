package com.francesco.chalettime.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.francesco.chalettime.data.repository.*;
import com.francesco.chalettime.model.*;
import com.francesco.chalettime.ui.common.BaseViewModel;
import java.util.*;

public class UserDetailsViewModel extends BaseViewModel {
    private final UserDetailsRepository users;
    private final WorkLogRepository logs;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<List<WorkLog>> workLogs = new MutableLiveData<>(Collections.emptyList());
    private boolean requested;
    private int pending;
    public UserDetailsViewModel() { this(new UserDetailsRepositoryImpl(), new WorkLogRepositoryImpl()); }
    public UserDetailsViewModel(UserDetailsRepository users, WorkLogRepository logs) { this.users = users; this.logs = logs; }
    public LiveData<User> getUser() { return user; }
    public LiveData<List<WorkLog>> getWorkLogs() { return workLogs; }
    private void complete() { if (!cleared && --pending == 0) loading.setValue(false); }
    public void load(String id, int month) {
        if (requested) return;
        if (id == null || month < 1 || month > 12) { message("Dati del dipendente mancanti"); return; }
        requested = true; pending = 2; loading.setValue(true);
        users.fetchUserDetailsById(id, new UserDetailsRepository.FetchUserDetailsByIdCallback() {
            @Override public void onSuccess(User result) { if (!cleared) { user.setValue(result); complete(); } }
            @Override public void onFailure(String error) { message(error); complete(); }
        });
        logs.getUserWorkLogsForMonth(id, Calendar.getInstance().get(Calendar.YEAR), month, new WorkLogRepository.GetUserWorkLogsCallback() {
            @Override public void onSuccess(List<WorkLog> result) {
                if (cleared) return;
                List<WorkLog> sorted = new ArrayList<>(result);
                sorted.sort(Comparator.comparing(WorkLog::getDate));
                workLogs.setValue(Collections.unmodifiableList(sorted)); complete();
            }
            @Override public void onFailure(String error) { message(error); complete(); }
        });
    }
}
