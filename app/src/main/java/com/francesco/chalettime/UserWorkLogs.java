package com.francesco.chalettime;

import java.util.List;

public class UserWorkLogs {
    private String userId;
    private List<WorkLog> logs;

    public UserWorkLogs() { }

    public UserWorkLogs(String userId, List<WorkLog> logs) {
        this.userId = userId;
        this.logs = logs;
    }

    public String getUserId() {
        return userId;
    }

    public List<WorkLog> getLogs() {
        return logs;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLogs(List<WorkLog> logs) {
        this.logs = logs;
    }
}
