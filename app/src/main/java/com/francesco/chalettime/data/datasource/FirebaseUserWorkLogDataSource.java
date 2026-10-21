package com.francesco.chalettime.data.datasource;

import com.francesco.chalettime.data.repository.UserWorkLogRepository;
import com.francesco.chalettime.model.UserWorkLogs;
import com.francesco.chalettime.model.WorkLog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FirebaseUserWorkLogDataSource {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public void saveWorkLog(String userId, WorkLog workLog, UserWorkLogRepository.SaveWorkLogCallback callback) {
        DocumentReference ref = firestore.collection("work_logs").document(userId);
        firestore.runTransaction(transaction -> {
            UserWorkLogs existing = transaction.get(ref).toObject(UserWorkLogs.class);
            List<WorkLog> logs = existing == null || existing.getLogs() == null
                    ? new ArrayList<>() : new ArrayList<>(existing.getLogs());
            logs.add(workLog);
            transaction.set(ref, new UserWorkLogs(userId, logs), com.google.firebase.firestore.SetOptions.merge());
            return null;
        }).addOnSuccessListener(result -> callback.onSuccess("Ore salvate correttamente"))
          .addOnFailureListener(error -> callback.onFailure(error.getMessage()));
    }
}
