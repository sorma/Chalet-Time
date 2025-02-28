package com.francesco.chalettime;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


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

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        TextInputLayout dateInputLayout = view.findViewById(R.id.date_input_layout);
        EditText dateEditText = view.findViewById(R.id.date);

        dateInputLayout.setEndIconVisible(!TextUtils.isEmpty(dateEditText.getText()));

        dateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dateInputLayout.setEndIconVisible(false);
            } else {
                dateInputLayout.setEndIconVisible(!TextUtils.isEmpty(dateEditText.getText()));
            }
        });

        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                dateInputLayout.setEndIconVisible(!TextUtils.isEmpty(editable));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        EditText hourEditText = view.findViewById(R.id.hour);
        EditText dateEditText = view.findViewById(R.id.date);

        dateEditText.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = sdf.format(selectedDate.getTime());

                        dateEditText.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

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