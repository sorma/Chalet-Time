package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.data.datasource.FirebaseEmployeeDataSource;
import com.francesco.chalettime.model.Employee;
import com.francesco.chalettime.model.UserWorkLogs;

import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final FirebaseEmployeeDataSource firebaseEmployeeDataSource;

    public EmployeeRepositoryImpl() {
        firebaseEmployeeDataSource = new FirebaseEmployeeDataSource();
    }

    @Override
    public void fetchAllUsers(FetchUsersCallback callback) {
        firebaseEmployeeDataSource.fetchAllUsers(callback);
    }

    @Override
    public void fetchUserWorkLogs(String userId, int year, int month, FetchWorkLogsCallback callback) {
        firebaseEmployeeDataSource.fetchUserWorkLogs(userId, year, month, callback);
    }
}
