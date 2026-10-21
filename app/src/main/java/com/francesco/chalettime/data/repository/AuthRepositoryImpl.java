package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.User;
import com.francesco.chalettime.data.datasource.FirebaseAuthDataSource;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuthDataSource source;
    public AuthRepositoryImpl() { source = new FirebaseAuthDataSource(); }
    @Override public String getCurrentUserId() { return source.getCurrentUserId(); }
    @Override public void signOut() { source.signOut(); }
    @Override public void signInWithEmailAndPassword(String email, String password, AuthCallback callback) {
        source.signInWithEmailAndPassword(email, password).addOnSuccessListener(result -> callback.onSuccess()).addOnFailureListener(callback::onFailure);
    }
    @Override public void createUserWithEmailAndPassword(String email, String password, AuthCallback callback) {
        source.createUserWithEmailAndPassword(email, password).addOnSuccessListener(result -> callback.onSuccess()).addOnFailureListener(callback::onFailure);
    }
    @Override public void sendPasswordResetEmail(String email, AuthCallback callback) {
        source.sendPasswordResetEmail(email).addOnSuccessListener(result -> callback.onSuccess()).addOnFailureListener(callback::onFailure);
    }
    @Override public void saveUserToFirestore(String id, User user, AuthCallback callback) { source.saveUserToFirestore(id, user, callback); }
}
