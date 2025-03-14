package com.francesco.chalettime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminFragment extends Fragment {

    private CustomRecyclerAdapter adapter;
    private List<Recycler_item> userList;
    private List<Recycler_item> filteredList;
    private int selectedMonth = -1;
    private FirebaseAuth mAuth;

    public AdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new CustomRecyclerAdapter(getContext(), userList);
        recyclerView.setAdapter(adapter);

        fetchUsersFromFirestore();

        TextInputEditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        AutoCompleteTextView autoCompleteMonth = view.findViewById(R.id.month);

        String[] options = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                options
        );

        autoCompleteMonth.setAdapter(adapter);

        autoCompleteMonth.setOnItemClickListener((parent, view1, position, id) -> {
            selectedMonth = position + 1;
            fetchUsersFromFirestore();
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

    private void fetchUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userList.clear();

        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getId();
                        String name = document.getString("name");
                        String surname = document.getString("surname");
                        String nameSurname = name + " " + surname;
                        String birthday = document.getString("dateOfBirth");
                        String role = document.getString("role");
                        String gender = document.getString("gender");

                        assert role != null;
                        if (role.equals("user")) {
                            int defaultImage;

                            assert gender != null;
                            if (gender.equals("Maschio")) {
                                defaultImage = R.drawable.user_man;
                                fetchUserWorkHours(userId, nameSurname, defaultImage, birthday);
                            } else {
                                defaultImage = R.drawable.user_woman;
                                fetchUserWorkHours(userId, nameSurname, defaultImage, birthday);
                            }

                        }
                    }

                    adapter.updateList(userList);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Errore nel recupero utenti", e));
    }

    private void fetchUserWorkHours(String userId, String nameSurname, int defaultImage, String birthday) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        db.collection("work_logs").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserWorkLogs userWorkLogs = documentSnapshot.toObject(UserWorkLogs.class);

                        if (userWorkLogs != null && userWorkLogs.getLogs() != null) {
                            double totalHours = 0;

                            for (WorkLog workLog : userWorkLogs.getLogs()) {
                                String date = workLog.getDate();
                                double hours = workLog.getHours();

                                if (date != null) {
                                    String[] dateParts = date.split("/");
                                    if (dateParts.length == 3) {
                                        int logMonth = Integer.parseInt(dateParts[1]);
                                        int logYear = Integer.parseInt(dateParts[2]);

                                        if (logYear == currentYear && (selectedMonth == -1 || logMonth == selectedMonth)) {
                                            totalHours += hours;
                                        }
                                    }
                                }
                            }

                            String hoursText = "Ore: " + totalHours;
                            userList.add(new Recycler_item(nameSurname, hoursText, defaultImage, birthday));

                            adapter.updateList(userList);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Errore nel recupero delle ore di lavoro", e));
    }

    private void filterUsers(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (Recycler_item user : userList) {
                String name = user.getName();

                if (name.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }

        adapter.updateList(filteredList);
    }
}