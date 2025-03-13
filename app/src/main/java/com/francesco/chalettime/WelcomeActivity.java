package com.francesco.chalettime;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        /*
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(WelcomeActivity.this, WelcomeActivity.class);
        }
        startActivity(intent);
        finish();*/
    }
}