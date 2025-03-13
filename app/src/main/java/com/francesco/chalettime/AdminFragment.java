package com.francesco.chalettime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminFragment extends Fragment {

    private CustomRecyclerAdapter adapter;
    private List<Recycler_item> userList;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userList = new ArrayList<>();
        adapter = new CustomRecyclerAdapter(getContext(), userList);
        recyclerView.setAdapter(adapter);

        fetchUsersFromFirestore();

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
                        String nameSurname = name +" "+surname;
                        String birthday = document.getString("dateOfBirth");
                        String role = document.getString("role");
                        String gender = document.getString("gender");

                        assert role != null;
                        if (role.equals("user")) {
                            int defaultImage;

                            assert gender != null;
                            if(gender.equals("Maschio")){
                                defaultImage = R.drawable.user_man;
                                fetchUserWorkHours(userId, nameSurname, defaultImage, birthday);
                            }
                            else{
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
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        db.collection("work_logs")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalHours = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String date = document.getString("date");
                        Double hours = document.getDouble("hours");

                        if (date != null && hours != null) {
                            String[] dateParts = date.split("/");
                            if (dateParts.length == 3) {
                                int logMonth = Integer.parseInt(dateParts[1]);
                                int logYear = Integer.parseInt(dateParts[2]);

                                if (logMonth == currentMonth && logYear == currentYear) {
                                    totalHours += hours; // Sommo le ore lavorate
                                }
                            }
                        }
                    }

                    String hoursText = "Ore: " + totalHours;

                    userList.add(new Recycler_item(nameSurname, hoursText, defaultImage, birthday));

                    adapter.updateList(userList);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Errore nel recupero delle ore di lavoro", e));
    }

}