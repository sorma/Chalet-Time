package com.francesco.chalettime.data.datasource;

import android.util.Log;
import com.francesco.chalettime.data.repository.UserDetailsRepository;
import com.francesco.chalettime.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUserDetailsDataSource {

    private final FirebaseFirestore firestore;

    public FirebaseUserDetailsDataSource() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void fetchUserDetails(String firstName, String lastName, UserDetailsRepository.FetchUserDetailsCallback callback) {
        firestore.collection("users")
                .whereEqualTo("name", firstName)
                .whereEqualTo("surname", lastName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                        User user = userDocument.toObject(User.class);
                        if (user != null) {
                            callback.onSuccess(user, userDocument.getId());
                        } else {
                            callback.onFailure("Errore nel convertire i dati dell'utente.");
                        }
                    } else {
                        callback.onFailure("Nessun utente trovato con quel nome.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseUserDetailsDataSource", "Errore nel recupero dei dati utente", e);
                    callback.onFailure("Errore nel recupero dei dati utente.");
                });
    }

    public void fetchUserDetailsById(String userId, final UserDetailsRepository.FetchUserDetailsByIdCallback callback) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Errore nel convertire i dati dell'utente.");
                        }
                    } else {
                        callback.onFailure("Nessun utente trovato con questo ID.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseUserDetailsDataSource", "Errore nel recupero dei dati utente", e);
                    callback.onFailure("Errore nel recupero dei dati utente.");
                });
    }
}