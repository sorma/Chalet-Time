package com.francesco.chalettime.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.francesco.chalettime.data.repository.*;
import com.francesco.chalettime.model.User;
import com.francesco.chalettime.ui.common.BaseViewModel;

public class MainViewModel extends BaseViewModel {
    private final AuthRepository auth;
    private final UserDetailsRepository users;
    private final MutableLiveData<String> role = new MutableLiveData<>();
    private boolean requested;
    public MainViewModel() { this(new AuthRepositoryImpl(), new UserDetailsRepositoryImpl()); }
    public MainViewModel(AuthRepository auth, UserDetailsRepository users) { this.auth = auth; this.users = users; }
    public LiveData<String> getRole() { return role; }
    public boolean hasSession() { return auth.getCurrentUserId() != null; }
    public void loadRole() {
        if (requested) return;
        String id = auth.getCurrentUserId();
        if (id == null) return;
        requested = true;
        loading.setValue(true);
        users.fetchUserDetailsById(id, new UserDetailsRepository.FetchUserDetailsByIdCallback() {
            @Override public void onSuccess(User user) {
                if (cleared) return;
                loading.setValue(false);
                role.setValue("admin".equals(user.getRole()) ? "admin" : "user");
            }
            @Override public void onFailure(String error) {
                if (cleared) return;
                loading.setValue(false); requested = false; message(error);
            }
        });
    }
}
