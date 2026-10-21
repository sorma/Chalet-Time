package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.WorkLog;

public interface UserWorkLogRepository {
    void saveWorkLog(String userId, WorkLog workLog, SaveWorkLogCallback callback);

    interface SaveWorkLogCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }
}
