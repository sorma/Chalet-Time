package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.Employee;
import com.francesco.chalettime.model.UserWorkLogs;

import java.util.List;

public interface EmployeeRepository {
    void fetchAllUsers(FetchUsersCallback callback);
    void fetchUserWorkLogs(String userId, int year, int month, FetchWorkLogsCallback callback);

    interface FetchUsersCallback {
        void onSuccess(List<Employee> employees);
        void onFailure(String errorMessage);
    }

    interface FetchWorkLogsCallback {
        void onSuccess(UserWorkLogs userWorkLogs);
        void onFailure(String errorMessage);
    }
}
