package com.francesco.chalettime.ui.home;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.francesco.chalettime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.fragment_container_view);
                        assert navHostFragment != null;
                        NavController navController = navHostFragment.getNavController();

                        if ("admin".equals(role)) {
                            navController.navigate(R.id.adminFragment);
                        } else {
                            navController.navigate(R.id.homeFragment);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("RoleCheck", "Errore nel recupero del ruolo utente: " + e.getMessage()));
    }
}
