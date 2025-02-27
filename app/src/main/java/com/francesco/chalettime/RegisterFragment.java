package com.francesco.chalettime;

import android.content.Intent;
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


public class RegisterFragment extends Fragment {

    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;


    public RegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextUsername = view.findViewById(R.id.username);
        editTextPassword = view.findViewById(R.id.password);

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            if(isUsernameOk(editTextUsername.toString())){
                if(isPasswordOk(editTextPassword.toString())){
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } else{
                    editTextPassword.setError("Insert the correct password");
                }
            } else{
                editTextUsername.setError("Insert the correct username");
            }
        });

        Button textbutton = view.findViewById(R.id.textButton);
        textbutton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment));
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