package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.User;
import com.francesco.chalettime.data.datasource.FirebaseAuthDataSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuthDataSource firebaseAuthDataSource;

    public AuthRepositoryImpl() {
        this.firebaseAuthDataSource = new FirebaseAuthDataSource();
    }

    @Override
    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return firebaseAuthDataSource.signInWithEmailAndPassword(email, password);
    }

    @Override
    public Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
        return firebaseAuthDataSource.createUserWithEmailAndPassword(email, password);
    }

    @Override
    public Task<Void> sendPasswordResetEmail(String email) {
        return firebaseAuthDataSource.sendPasswordResetEmail(email);
    }

    @Override
    public void saveUserToFirestore(String userId, User user, AuthCallback callback) {
        firebaseAuthDataSource.saveUserToFirestore(userId, user, callback);
    }
}
