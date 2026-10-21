package com.francesco.chalettime.ui.welcome.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.francesco.chalettime.R;
import com.francesco.chalettime.model.User;
import com.francesco.chalettime.data.repository.AuthRepository;
import com.francesco.chalettime.data.repository.AuthRepositoryImpl;
import com.francesco.chalettime.ui.home.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextName;
    private TextInputEditText editTextSurname;
    private TextInputEditText editTextDate;
    private AutoCompleteTextView autoCompleteGender;
    private AuthRepository authRepository;

    public RegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authRepository = new AuthRepositoryImpl();
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

    @SuppressLint("CutPasteId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
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

        autoCompleteGender = view.findViewById(R.id.gender);
        String[] options = {"Femmina", "Maschio"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                options
        );
        autoCompleteGender.setAdapter(adapter);

        editTextEmail = view.findViewById(R.id.e_mail);
        editTextPassword = view.findViewById(R.id.password);
        editTextName = view.findViewById(R.id.name);
        editTextSurname = view.findViewById(R.id.surname);
        editTextDate = view.findViewById(R.id.date);

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            if (!validateFields()) {
                return;
            }

            String email, password, name, surname, date, gender;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            name = String.valueOf(editTextName.getText());
            surname = String.valueOf(editTextSurname.getText());
            date = String.valueOf(editTextDate.getText());
            gender = String.valueOf(autoCompleteGender.getText());

            authRepository.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                User newUser = new User(name, surname, date, email, (email.equals("admin@gmail.com") ? "admin" : "user"), gender);
                                authRepository.saveUserToFirestore(user.getUid(), newUser, new AuthRepository.AuthCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("RegisterFragment", "Utente salvato su Firestore");
                                        Toast.makeText(getContext(), "Registrazione avvenuta con successo!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("RegisterFragment", "Errore nel salvataggio utente su Firestore: " + e.getMessage());
                                        Toast.makeText(getContext(), "Registrazione fallita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getContext(), "Registrazione fallita: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        Button textbutton = view.findViewById(R.id.textButton);
        textbutton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment));
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (Objects.requireNonNull(editTextName.getText()).toString().trim().isEmpty()) {
            editTextName.setError("Inserisci il nome");
            return false;
        }

        if (Objects.requireNonNull(editTextSurname.getText()).toString().trim().isEmpty()) {
            editTextSurname.setError("Inserisci il cognome");
            return false;
        }

        if (Objects.requireNonNull(editTextDate.getText()).toString().trim().isEmpty()) {
            editTextDate.setError("Seleziona la tua data di nascita");
            return false;
        }

        if (Objects.requireNonNull(editTextEmail.getText()).toString().trim().isEmpty()) {
            editTextEmail.setError("Inserisci l'e-mail");
            return false;
        }

        if (Objects.requireNonNull(editTextPassword.getText()).toString().trim().isEmpty()) {
            editTextPassword.setError("Inserisci la password");
            return false;
        }

        return isValid;
    }
}