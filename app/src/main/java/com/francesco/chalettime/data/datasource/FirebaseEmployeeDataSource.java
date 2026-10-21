package com.francesco.chalettime.data.datasource;

import android.util.Log;

import com.francesco.chalettime.R;
import com.francesco.chalettime.data.repository.EmployeeRepository;
import com.francesco.chalettime.model.Employee;
import com.francesco.chalettime.model.UserWorkLogs;
import com.francesco.chalettime.model.WorkLog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FirebaseEmployeeDataSource {

    private final FirebaseFirestore firestore;

    public FirebaseEmployeeDataSource() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void fetchAllUsers(EmployeeRepository.FetchUsersCallback callback) {
        List<Employee> tempList = new ArrayList<>();
        firestore.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getId();
                        String name = document.getString("name");
                        String surname = document.getString("surname");
                        String nameSurname = name + " " + surname;
                        String birthday = document.getString("dateOfBirth");
                        String role = document.getString("role");
                        String gender = document.getString("gender");

                        if ("user".equals(role)) {
                            int defaultImage = (gender != null && gender.equals("Maschio")) ?
                                    R.drawable.user_man : R.drawable.user_woman;
                            tempList.add(new Employee(userId, nameSurname, "", defaultImage, birthday)); // Aggiungiamo userId
                        }
                    }
                    callback.onSuccess(tempList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Errore nel recupero utenti", e);
                    callback.onFailure("Errore nel recupero utenti");
                });
    }

    public void fetchUserWorkLogs(String userId, int year, int month, EmployeeRepository.FetchWorkLogsCallback callback) {
        firestore.collection("work_logs").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    UserWorkLogs userWorkLogs = documentSnapshot.toObject(UserWorkLogs.class);
                    callback.onSuccess(userWorkLogs);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Errore nel recupero delle ore di lavoro per l'utente " + userId, e);
                    callback.onFailure("Errore nel recupero delle ore di lavoro");
                });
    }
}
