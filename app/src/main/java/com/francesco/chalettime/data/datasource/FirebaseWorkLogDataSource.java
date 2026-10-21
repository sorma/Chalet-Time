package com.francesco.chalettime.data.datasource;

import android.util.Log;
import com.francesco.chalettime.data.repository.WorkLogRepository;
import com.francesco.chalettime.model.UserWorkLogs;
import com.francesco.chalettime.model.WorkLog;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FirebaseWorkLogDataSource {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public void getUserWorkLogsForMonth(String userId, int year, int month, WorkLogRepository.GetUserWorkLogsCallback callback) {
        firestore.collection("work_logs").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserWorkLogs userWorkLogs = documentSnapshot.toObject(UserWorkLogs.class);
                        List<WorkLog> monthlyLogs = new ArrayList<>();
                        if (userWorkLogs != null && userWorkLogs.getLogs() != null) {
                            for (WorkLog log : userWorkLogs.getLogs()) {
                                if (log.getDate() != null) {
                                    String[] dateParts = log.getDate().split("/");
                                    if (dateParts.length == 3) {
                                        try {
                                            int logMonth = Integer.parseInt(dateParts[1]);
                                            int logYear = Integer.parseInt(dateParts[2]);
                                            if (logYear == year && logMonth == month) {
                                                monthlyLogs.add(log);
                                            }
                                        } catch (NumberFormatException e) {
                                            Log.w("FirebaseWorkLogDS", "Data non valida: " + log.getDate());
                                        }
                                    }
                                }
                            }
                        }
                        callback.onSuccess(monthlyLogs);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Errore nel recupero delle ore di lavoro per il mese: " + e.getMessage()));
    }
}