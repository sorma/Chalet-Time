package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.User;

public interface UserDetailsRepository {
    void fetchUserDetails(String firstName, String lastName, FetchUserDetailsCallback callback);
    void fetchUserDetailsById(String userId, FetchUserDetailsByIdCallback callback); // Nuovo metodo

    interface FetchUserDetailsCallback {
        void onSuccess(User user, String userId);
        void onFailure(String errorMessage);
    }

    interface FetchUserDetailsByIdCallback {
        void onSuccess(User user); // Callback modificato, non serve l'userId qui
        void onFailure(String errorMessage);
    }
}