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
import androidx.lifecycle.ViewModelProvider;
import com.francesco.chalettime.ui.welcome.viewmodel.AuthViewModel;

import com.francesco.chalettime.ui.home.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private String email, password;
    private AuthViewModel viewModel;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
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

            viewModel.login(email, password);
        });

        viewModel.getMessages().observe(getViewLifecycleOwner(), event -> {
            String message = event.consume();
            if (message != null) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            view.findViewById(R.id.login_button).setEnabled(!loading);
            view.findViewById(R.id.textButton).setEnabled(!loading);
            view.findViewById(R.id.forgetButton).setEnabled(!loading);
            view.findViewById(R.id.progress).setVisibility(loading ? View.VISIBLE : View.GONE);
        });
        viewModel.getAuthenticated().observe(getViewLifecycleOwner(), event -> {
            if (event.consume() == null) return;
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button textbutton = view.findViewById(R.id.textButton);
        textbutton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));

        Button forgetButton = view.findViewById(R.id.forgetButton);
        forgetButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Inserisci il tuo indirizzo e-mail", Toast.LENGTH_SHORT).show();
            } else {
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        viewModel.resetPassword(email);
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (Objects.requireNonNull(editTextEmail.getText()).toString().trim().isEmpty()) {
            editTextEmail.setError("Inserisci l’e-mail");
            return false;
        }

        if (Objects.requireNonNull(editTextPassword.getText()).toString().trim().isEmpty()) {
            editTextPassword.setError("Inserisci la password");
            return false;
        }

        return isValid;
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        editTextEmail = null;
        editTextPassword = null;
    }
}