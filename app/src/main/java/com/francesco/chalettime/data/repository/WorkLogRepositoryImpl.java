package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.data.datasource.FirebaseWorkLogDataSource;
import com.francesco.chalettime.model.WorkLog;
import java.util.List;
import java.util.Calendar;

public class WorkLogRepositoryImpl implements WorkLogRepository {

    private final FirebaseWorkLogDataSource firebaseWorkLogDataSource;

    public WorkLogRepositoryImpl() {
        firebaseWorkLogDataSource = new FirebaseWorkLogDataSource();
    }

    @Override
    public void getUserWorkLogsForMonth(String userId, int year, int month, GetUserWorkLogsCallback callback) {
        firebaseWorkLogDataSource.getUserWorkLogsForMonth(userId, year, month, callback);
    }
}