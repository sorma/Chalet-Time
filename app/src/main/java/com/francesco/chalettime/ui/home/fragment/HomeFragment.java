package com.francesco.chalettime.ui.home.fragment;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.francesco.chalettime.R;
import com.francesco.chalettime.adapter.HoursAdapter;
import com.francesco.chalettime.data.repository.UserWorkLogRepository;
import com.francesco.chalettime.data.repository.UserWorkLogRepositoryImpl;
import com.francesco.chalettime.model.WorkLog;
import com.francesco.chalettime.ui.welcome.WelcomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private UserWorkLogRepository workLogRepository; // Nuovo repository

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workLogRepository = new UserWorkLogRepositoryImpl(); // Inizializza il repository
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ... (il tuo codice onCreateView rimane simile)
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextInputLayout hourInputLayout = view.findViewById(R.id.hourInputLayout);
        TextInputEditText hourEditText = view.findViewById(R.id.hour);
        TextInputLayout dateInputLayout = view.findViewById(R.id.date_input_layout);
        EditText dateEditText = view.findViewById(R.id.date);

        hourInputLayout.setEndIconVisible(!TextUtils.isEmpty(hourEditText.getText()));
        hourEditText.setOnFocusChangeListener((v, hasFocus) -> hourInputLayout.setEndIconVisible(!hasFocus && !TextUtils.isEmpty(hourEditText.getText())));
        hourEditText.addTextChangedListener(new TextWatcher() { @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {} @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {} @Override public void afterTextChanged(Editable editable) { hourInputLayout.setEndIconVisible(!TextUtils.isEmpty(editable)); } });

        dateInputLayout.setEndIconVisible(!TextUtils.isEmpty(dateEditText.getText()));
        dateEditText.setOnFocusChangeListener((v, hasFocus) -> dateInputLayout.setEndIconVisible(!hasFocus && !TextUtils.isEmpty(dateEditText.getText())));
        dateEditText.addTextChangedListener(new TextWatcher() { @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {} @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {} @Override public void afterTextChanged(Editable editable) { dateInputLayout.setEndIconVisible(!TextUtils.isEmpty(editable)); } });

        return view;
    }

    @SuppressLint("SetTextI18n")
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(selectedYear, selectedMonth, selectedDay);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = sdf.format(selectedDate.getTime());
                dateEditText.setText(formattedDate);
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        hourEditText.setOnClickListener(v -> {
            List<String> options = new ArrayList<>();
            for (int i = 1; i <= 16; i++) {
                options.add(String.valueOf(i));
                if (i != 16) {
                    options.add(i + ".5");
                }
            }
            HoursAdapter adapter = new HoursAdapter(requireContext(), options);
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_hours_body, null);
            GridView dialogGridView = dialogView.findViewById(R.id.gridView);
            Button okButton = dialogView.findViewById(R.id.okButton);
            Button cancelButton = dialogView.findViewById(R.id.negativeButton);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            @SuppressLint("InflateParams") View titleView = inflater.inflate(R.layout.dialog_hours_title, null);
            TextView titleTextView = titleView.findViewById(R.id.dialog_title);
            builder.setCustomTitle(titleView);
            builder.setView(dialogView);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialogGridView.setAdapter(adapter);
            dialogGridView.setOnItemClickListener((parent, a, position, id) -> {
                String selectedOption = options.get(position);
                titleTextView.setText("Selected: " + selectedOption);
                adapter.setSelectedPosition(position);
            });
            okButton.setOnClickListener(v1 -> {
                int selectedPos = adapter.getSelectedPosition();
                if (selectedPos != -1) {
                    String selectedOption = options.get(selectedPos);
                    hourEditText.setText(selectedOption);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Seleziona un'ora prima di confermare", Toast.LENGTH_SHORT).show();
                }
            });
            cancelButton.setOnClickListener(v1 -> dialog.dismiss());
        });


        Button confirmButton = view.findViewById(R.id.confrim_button);
        confirmButton.setOnClickListener(view1 -> {
            String date = String.valueOf(dateEditText.getText());
            String textHours = String.valueOf(hourEditText.getText());
            double hours = 0;
            if (!textHours.isEmpty()) {
                try {
                    hours = Double.parseDouble(textHours);
                } catch (NumberFormatException e) {
                    Log.e("CastError", "Casting error: " + e.getMessage(), e);
                }
            }
            saveWorkHours(date, hours);
            dateEditText.setText("");
            hourEditText.setText("");
        });

        ImageButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent intent = new Intent(getContext(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void saveWorkHours(String selectedDate, double hoursWorked) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "Utente non autenticato", Toast.LENGTH_SHORT).show();
            return;
        }

        WorkLog workLog = new WorkLog(userId, selectedDate, hoursWorked);
        workLogRepository.saveWorkLog(userId, workLog, new UserWorkLogRepository.SaveWorkLogCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Errore nel salvataggio: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}