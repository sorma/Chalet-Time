package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.WorkLog;
import java.util.List;

public interface WorkLogRepository {
    void getUserWorkLogsForMonth(String userId, int year, int month, GetUserWorkLogsCallback callback);

    interface GetUserWorkLogsCallback {
        void onSuccess(List<WorkLog> workLogs);
        void onFailure(String errorMessage);
    }
}