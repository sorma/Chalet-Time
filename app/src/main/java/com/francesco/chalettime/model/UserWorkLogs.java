package com.francesco.chalettime.model;

import java.util.List;

public class UserWorkLogs {
    private String userId;
    private List<WorkLog> logs;

    public UserWorkLogs() { }

    public UserWorkLogs(String userId, List<WorkLog> logs) {
        this.userId = userId;
        this.logs = logs;
    }


    public List<WorkLog> getLogs() {
        return logs;
    }

}
