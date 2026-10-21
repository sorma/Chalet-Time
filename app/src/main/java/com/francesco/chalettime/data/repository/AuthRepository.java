package com.francesco.chalettime.data.repository;

import com.francesco.chalettime.model.User;

/** App-facing authentication contract, independent of the Firebase SDK. */
public interface AuthRepository {
    String getCurrentUserId();
    void signOut();
    void signInWithEmailAndPassword(String email, String password, AuthCallback callback);
    void createUserWithEmailAndPassword(String email, String password, AuthCallback callback);
    void sendPasswordResetEmail(String email, AuthCallback callback);
    void saveUserToFirestore(String userId, User user, AuthCallback callback);
    interface AuthCallback {
        void onSuccess();
        void onFailure(Exception error);
    }
}
