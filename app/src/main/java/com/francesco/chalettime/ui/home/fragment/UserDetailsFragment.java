package com.francesco.chalettime.ui.home.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.francesco.chalettime.R;
import com.francesco.chalettime.adapter.WorkLogAdapter;
import com.francesco.chalettime.data.repository.UserDetailsRepository;
import com.francesco.chalettime.data.repository.UserDetailsRepositoryImpl;
import com.francesco.chalettime.data.repository.WorkLogRepository;
import com.francesco.chalettime.data.repository.WorkLogRepositoryImpl;
import com.francesco.chalettime.model.User;
import com.francesco.chalettime.model.WorkLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserDetailsFragment extends Fragment {

    private String userName;
    private String userId; // Ricevi l'userId
    private WorkLogAdapter adapter;
    private List<WorkLog> workLogs;
    private TextView userNameTextView;
    private TextView birthdayTextView;
    private ImageView genderImageView;
    private int selectedMonth;
    private UserDetailsRepository userDetailsRepository;
    private WorkLogRepository workLogRepository;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDetailsRepository = new UserDetailsRepositoryImpl();
        workLogRepository = new WorkLogRepositoryImpl();
        Bundle args = getArguments();
        if (args != null) {
            userName = args.getString("userName");
            selectedMonth = args.getInt("selectedMonth");
            userId = args.getString("userId"); // Recupera l'userId dal Bundle
        } else {
            Log.e("UserDetailsFragment", "Argomenti mancanti nel Bundle");
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
            Bundle args = new Bundle();
            args.putInt("selectedMonth", selectedMonth);
            Navigation.findNavController(view1).navigate(R.id.action_userDetailsFragment_to_adminFragment, args);
        });

        if (userId != null) {
            fetchUserDetails(userId); // Usa l'userId per caricare i dettagli
            fetchWorkLogs(userId, selectedMonth); // Usa l'userId per caricare le ore
        }
    }

    @SuppressLint("SetTextI18n")
    private void fetchUserDetails(String userId) {
        userDetailsRepository.fetchUserDetailsById(userId, new UserDetailsRepository.FetchUserDetailsByIdCallback() {
            @Override
            public void onSuccess(User user) {
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
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("UserDetailsFragment", "Errore nel recupero dei dati utente: " + errorMessage);
                Toast.makeText(getContext(), "Errore nel recupero dei dati utente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchWorkLogs(String userId, int month) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        workLogRepository.getUserWorkLogsForMonth(userId, currentYear, month, new WorkLogRepository.GetUserWorkLogsCallback() {
            @Override
            public void onSuccess(List<WorkLog> logs) {
                workLogs.clear();
                workLogs.addAll(logs);
                workLogs.sort((log1, log2) -> log1.getDate().compareTo(log2.getDate()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("UserDetailsFragment", "Errore nel recupero delle ore di lavoro: " + errorMessage);
                Toast.makeText(getContext(), "Errore nel recupero delle ore di lavoro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}