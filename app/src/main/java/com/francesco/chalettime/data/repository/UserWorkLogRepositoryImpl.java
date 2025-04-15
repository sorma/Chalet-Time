package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.data.datasource.FirebaseUserWorkLogDataSource;
import com.francesco.chalettime.model.WorkLog;

public class UserWorkLogRepositoryImpl implements UserWorkLogRepository {

    private final FirebaseUserWorkLogDataSource firebaseWorkLogDataSource;

    public UserWorkLogRepositoryImpl() {
        firebaseWorkLogDataSource = new FirebaseUserWorkLogDataSource();
    }

    @Override
    public void saveWorkLog(String userId, WorkLog workLog, SaveWorkLogCallback callback) {
        firebaseWorkLogDataSource.saveWorkLog(userId, workLog, callback);
    }
}