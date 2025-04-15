package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public interface AuthRepository {
    Task<AuthResult> signInWithEmailAndPassword(String email, String password);
    Task<AuthResult> createUserWithEmailAndPassword(String email, String password);
    Task<Void> sendPasswordResetEmail(String email);
    void saveUserToFirestore(String userId, User user, AuthCallback callback);

    interface AuthCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
