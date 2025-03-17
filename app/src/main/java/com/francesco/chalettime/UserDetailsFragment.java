package com.francesco.chalettime;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsFragment extends Fragment {

    private String userName;
    private WorkLogAdapter adapter;
    private List<WorkLog> workLogs;
    private TextView userNameTextView;
    private TextView birthdayTextView;
    private ImageView genderImageView;
    private int selectedMonth; // Aggiungi questa variabile

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    public static UserDetailsFragment newInstance(String userName, int selectedMonth) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        args.putInt("selectedMonth", selectedMonth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString("userName");
            selectedMonth = getArguments().getInt("selectedMonth"); // Recupera selectedMonth
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workLogs = new ArrayList<>();
        adapter = new WorkLogAdapter(workLogs);
        recyclerView.setAdapter(adapter);

        userNameTextView = view.findViewById(R.id.name);
        genderImageView = view.findViewById(R.id.imageView);
        birthdayTextView = view.findViewById(R.id.birthday);
        ImageButton backImageButton = view.findViewById(R.id.imageButton);

        backImageButton.setOnClickListener(view1 -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        fetchUserDataAndWorkLogs();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void fetchUserDataAndWorkLogs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (userName == null || userName.trim().isEmpty()) {
            Log.e("UserDetailsFragment", "Errore: userName è nullo o vuoto.");
            return;
        }

        String[] nameParts = userName.trim().split(" ");
        if (nameParts.length < 2) {
            Log.e("UserDetailsFragment", "Errore: userName non contiene nome e cognome.");
            return;
        }

        String firstName = nameParts[0];
        String lastName = nameParts[1];

        db.collection("users")
                .whereEqualTo("name", firstName)
                .whereEqualTo("surname", lastName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                        User user = userDocument.toObject(User.class);

                        if (user != null) {
                            userNameTextView.setText(user.getName() + " " + user.getSurname());

                            String gender = user.getGender();
                            if (gender != null) {
                                if (gender.equals("Maschio")) {
                                    genderImageView.setImageResource(R.drawable.user_man);
                                } else if (gender.equals("Femmina")) {
                                    genderImageView.setImageResource(R.drawable.user_woman);
                                }
                            }

                            birthdayTextView.setText(user.getDateOfBirth());

                            // Recupera le ore di lavoro filtrate per mese e ordina per data
                            String userId = userDocument.getId();
                            db.collection("work_logs").document(userId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            UserWorkLogs userWorkLogs = documentSnapshot.toObject(UserWorkLogs.class);
                                            if (userWorkLogs != null && userWorkLogs.getLogs() != null) {
                                                List<WorkLog> logs = userWorkLogs.getLogs();
                                                List<WorkLog> filteredLogs = new ArrayList<>();

                                                // Filtra i work logs per mese
                                                for (WorkLog log : logs) {
                                                    String date = log.getDate();
                                                    if (date != null) {
                                                        String[] dateParts = date.split("/");
                                                        if (dateParts.length == 3) {
                                                            int logMonth = Integer.parseInt(dateParts[1]);
                                                            if (logMonth == selectedMonth) {
                                                                filteredLogs.add(log);
                                                            }
                                                        }
                                                    }
                                                }

                                                // Ordina la lista di work logs filtrati per data
                                                filteredLogs.sort((log1, log2) -> {
                                                    String date1 = log1.getDate();
                                                    String date2 = log2.getDate();
                                                    return date1.compareTo(date2); // Ordine crescente (vecchio -> nuovo)
                                                });

                                                workLogs.addAll(filteredLogs);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("UserDetailsFragment", "Errore nel recupero delle work logs", e));
                        }
                    } else {
                        Log.e("UserDetailsFragment", "Nessun utente trovato con quel nome.");
                    }
                })
                .addOnFailureListener(e -> Log.e("UserDetailsFragment", "Errore nel recupero dei dati utente", e));
    }
}