package com.francesco.chalettime;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextInputLayout hourInputLayout = view.findViewById(R.id.hourInputLayout);
        TextInputEditText hourEditText = view.findViewById(R.id.hour);
        TextInputLayout dateInputLayout = view.findViewById(R.id.date_input_layout);
        EditText dateEditText = view.findViewById(R.id.date);


        hourInputLayout.setEndIconVisible(!TextUtils.isEmpty(hourEditText.getText()));

        hourEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hourInputLayout.setEndIconVisible(false);
            } else {
                hourInputLayout.setEndIconVisible(!TextUtils.isEmpty(hourEditText.getText()));
            }
        });

        hourEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                hourInputLayout.setEndIconVisible(!TextUtils.isEmpty(editable));
            }
        });

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

        hourEditText.setOnClickListener(v -> {
            List<String> options = new ArrayList<>();
            for (int i = 1; i <= 16; i++) {
                options.add(String.valueOf(i));
                if (i != 16) {
                    options.add(i + ".5");
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.grid_item, R.id.numberText, options);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_hours_body, null);
            GridView dialogGridView = dialogView.findViewById(R.id.gridView);
            Button okButton = dialogView.findViewById(R.id.okButton);
            Button cancelButton = dialogView.findViewById(R.id.negativeButton);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            @SuppressLint("InflateParams")
            View titleView = getLayoutInflater().inflate(R.layout.dialog_title_centered, null);
            TextView titleTextView = titleView.findViewById(R.id.dialog_title);
            builder.setCustomTitle(titleView);
            builder.setView(dialogView);
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.show();

            dialogGridView.setAdapter(adapter);

            final int[] selectedPosition = {-1}; 
            dialogGridView.setOnItemClickListener((parent, view1, position, id) -> {
                selectedPosition[0] = position;
                String selectedOption = options.get(position);

                titleTextView.setText("Hour selected: " + selectedOption);

                for (int i = 0; i < parent.getChildCount(); i++) {
                    View itemView = parent.getChildAt(i);
                    if (itemView != null) {
                        TextView numberText = itemView.findViewById(R.id.numberText);
                        if (i == position) {
                            numberText.setSelected(true);
                            numberText.setTextColor(Color.WHITE);
                            itemView.setBackgroundResource(R.drawable.circle_selector);
                        } else {
                            numberText.setSelected(false);
                            numberText.setTextColor(Color.BLACK);
                            itemView.setBackgroundResource(R.drawable.circle_selector);
                        }
                    }
                }
            });

            okButton.setOnClickListener(v1 -> {
                if (selectedPosition[0] != -1) {
                    String selectedOption = options.get(selectedPosition[0]);
                    hourEditText.setText(selectedOption);
                    dialog.dismiss();
                }
            });

            cancelButton.setOnClickListener(v1 -> dialog.dismiss());
        });



    }
}