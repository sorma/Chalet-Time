package com.francesco.chalettime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;


public class LoginFragment extends Fragment {

    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextUsername = view.findViewById(R.id.username);
        editTextPassword = view.findViewById(R.id.password);

        Button loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            if(isUsernameOk(editTextUsername.toString())){
                if(isPasswordOk(editTextPassword.toString())){
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainActivity);
                } else{
                    editTextPassword.setError("Insert the correct password");
                }
            } else{
                editTextUsername.setError("Insert the correct username");
            }
        });

    }

    private boolean isUsernameOk(String email){
        //Controlli con il database che l'username inserito esista
        return true;
    }
    private boolean isPasswordOk(String password){
        //Controlli che la password corrisponda all'username inserito
        return true;
    }
}