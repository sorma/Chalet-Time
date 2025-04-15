package com.francesco.chalettime.ui.welcome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.francesco.chalettime.R;
import com.francesco.chalettime.data.repository.AuthRepository;
import com.francesco.chalettime.data.repository.AuthRepositoryImpl;
import com.francesco.chalettime.ui.home.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private String email, password;
    private AuthRepository authRepository;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authRepository = new AuthRepositoryImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextEmail = view.findViewById(R.id.e_mail);
        editTextPassword = view.findViewById(R.id.password);

        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            if (!validateFields()) {
                return;
            }

            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            authRepository.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "signInWithEmail:success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "signInWithEmail:failure", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        Button textbutton = view.findViewById(R.id.textButton);
        textbutton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));

        Button forgetButton = view.findViewById(R.id.forgetButton);
        forgetButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your email address", Toast.LENGTH_SHORT).show();
            } else {
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        authRepository.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (Objects.requireNonNull(editTextEmail.getText()).toString().trim().isEmpty()) {
            editTextEmail.setError("Enter the e-mail");
            return false;
        }

        if (Objects.requireNonNull(editTextPassword.getText()).toString().trim().isEmpty()) {
            editTextPassword.setError("Enter the password");
            return false;
        }

        return isValid;
    }
}