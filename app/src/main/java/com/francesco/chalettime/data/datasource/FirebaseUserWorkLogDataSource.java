package com.francesco.chalettime.data.datasource;

import android.util.Log;

import com.francesco.chalettime.data.repository.UserWorkLogRepository;
import com.francesco.chalettime.model.UserWorkLogs;
import com.francesco.chalettime.model.WorkLog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseUserWorkLogDataSource {

    private final FirebaseFirestore firestore;

    public FirebaseUserWorkLogDataSource() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void saveWorkLog(String userId, WorkLog workLog, UserWorkLogRepository.SaveWorkLogCallback callback) {
        DocumentReference workLogRef = firestore.collection("work_logs").document(userId);

        workLogRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<WorkLog> currentLogs = Objects.requireNonNull(documentSnapshot.toObject(UserWorkLogs.class)).getLogs();
                currentLogs.add(workLog);
                workLogRef.update("logs", currentLogs)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirebaseWorkLogDataSource", "Data updated");
                            callback.onSuccess("Data updated");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseWorkLogDataSource", "Update failed: " + e.getMessage());
                            callback.onFailure("Update failed: " + e.getMessage());
                        });
            } else {
                List<WorkLog> logs = new ArrayList<>();
                logs.add(workLog);
                UserWorkLogs userWorkLogs = new UserWorkLogs(userId, logs);
                workLogRef.set(userWorkLogs)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirebaseWorkLogDataSource", "Data saved");
                            callback.onSuccess("Data saved");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseWorkLogDataSource", "Save failed: " + e.getMessage());
                            callback.onFailure("Save failed: " + e.getMessage());
                        });
            }
        }).addOnFailureListener(e -> {
            Log.e("FirebaseWorkLogDataSource", "Error getting document: " + e.getMessage());
            callback.onFailure("Error: " + e.getMessage());
        });
    }
}
