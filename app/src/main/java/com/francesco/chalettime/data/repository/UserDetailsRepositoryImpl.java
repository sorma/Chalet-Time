package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.data.datasource.FirebaseUserDetailsDataSource;
import com.francesco.chalettime.model.User;

public class UserDetailsRepositoryImpl implements UserDetailsRepository {

    private final FirebaseUserDetailsDataSource firebaseUserDetailsDataSource;

    public UserDetailsRepositoryImpl() {
        firebaseUserDetailsDataSource = new FirebaseUserDetailsDataSource();
    }

    @Override
    public void fetchUserDetails(String firstName, String lastName, FetchUserDetailsCallback callback) {
        firebaseUserDetailsDataSource.fetchUserDetails(firstName, lastName, callback);
    }

    @Override
    public void fetchUserDetailsById(String userId, FetchUserDetailsByIdCallback callback) {
        firebaseUserDetailsDataSource.fetchUserDetailsById(userId, callback);
    }
}
