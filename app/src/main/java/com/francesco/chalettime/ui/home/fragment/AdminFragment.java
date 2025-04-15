package com.francesco.chalettime.ui.home.fragment;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.francesco.chalettime.R;
import com.francesco.chalettime.adapter.EmployeeAdapter;
import com.francesco.chalettime.data.repository.EmployeeRepository;
import com.francesco.chalettime.data.repository.EmployeeRepositoryImpl;
import com.francesco.chalettime.model.Employee;
import com.francesco.chalettime.model.UserWorkLogs;
import com.francesco.chalettime.model.WorkLog;
import com.francesco.chalettime.ui.home.viewmodel.AdminViewModel;
import com.francesco.chalettime.ui.welcome.WelcomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminFragment extends Fragment implements EmployeeAdapter.OnItemClickListener {

    private EmployeeAdapter adapter;
    private List<Employee> userList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private AutoCompleteTextView autoCompleteMonth;
    private ArrayAdapter<String> monthAdapter;
    private EmployeeRepository employeeRepository;
    private AdminViewModel viewModel;

    public AdminFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        employeeRepository = new EmployeeRepositoryImpl();
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new EmployeeAdapter(getContext(), userList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        TextInputEditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        autoCompleteMonth = view.findViewById(R.id.month);
        String[] months = getResources().getStringArray(R.array.months);
        monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, months);
        autoCompleteMonth.setAdapter(monthAdapter);

        int selectedMonth = viewModel.getSelectedMonth().getValue();
        if (selectedMonth == -1) {
            if (getArguments() != null && getArguments().containsKey("selectedMonth")) {
                selectedMonth = getArguments().getInt("selectedMonth");
            } else {
                Calendar calendar = Calendar.getInstance();
                selectedMonth = calendar.get(Calendar.MONTH) + 1;
            }
            viewModel.setSelectedMonth(selectedMonth);
        }

        autoCompleteMonth.setText(getMonthName(selectedMonth), false);
        userList = viewModel.getEmployeeList().getValue();
        if (userList == null || userList.isEmpty()) {
            fetchUsersData(selectedMonth);
        } else {
            adapter.updateList(new ArrayList<>(userList));
        }

        autoCompleteMonth.setOnItemClickListener((parent, view1, position, id) -> {
            int newSelectedMonth = position + 1;
            viewModel.setSelectedMonth(newSelectedMonth);
            fetchUsersData(newSelectedMonth);
        });

        autoCompleteMonth.setOnClickListener(v -> autoCompleteMonth.showDropDown());

        ImageButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent intent = new Intent(getContext(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private String getMonthName(int month) {
        String[] months = getResources().getStringArray(R.array.months);
        return months[month - 1];
    }

    @Override
    public void onItemClick(Employee user) {
        int currentMonth = viewModel.getSelectedMonth().getValue();
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("userName", user.getName());
        args.putInt("selectedMonth", currentMonth);
        args.putString("userId", user.getUserId());
        navController.navigate(R.id.action_adminFragment_to_userDetailsFragment, args);
    }

    private void fetchUsersData(int month) {
        employeeRepository.fetchAllUsers(new EmployeeRepository.FetchUsersCallback() {
            @Override
            public void onSuccess(List<Employee> employees) {
                List<Employee> updatedList = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);

                for (Employee employee : employees) {
                    employeeRepository.fetchUserWorkLogs(employee.getUserId(), currentYear, month,
                            new EmployeeRepository.FetchWorkLogsCallback() {
                                @Override
                                public void onSuccess(UserWorkLogs userWorkLogs) {
                                    double totalHours = 0;
                                    if (userWorkLogs != null && userWorkLogs.getLogs() != null) {
                                        for (WorkLog workLog : userWorkLogs.getLogs()) {
                                            String date = workLog.getDate();
                                            double hours = workLog.getHours();
                                            if (date != null) {
                                                String[] dateParts = date.split("/");
                                                if (dateParts.length == 3) {
                                                    int logMonth = Integer.parseInt(dateParts[1]);
                                                    int logYear = Integer.parseInt(dateParts[2]);
                                                    if (logYear == currentYear && (month == -1 || logMonth == month)) {
                                                        totalHours += hours;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    employee.setHours("Ore: " + totalHours);
                                    updatedList.add(employee);
                                    checkAndUpdateUI(updatedList, employees.size());
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    employee.setHours("Ore: 0");
                                    updatedList.add(employee);
                                    checkAndUpdateUI(updatedList, employees.size());
                                }
                            });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Errore nel recupero degli utenti", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndUpdateUI(List<Employee> list, int expectedSize) {
        if (list.size() == expectedSize) {
            userList = new ArrayList<>(list);
            adapter.updateList(userList);
            viewModel.setEmployeeList(userList);
        }
    }

    private void filterUsers(String query) {
        List<Employee> filteredList = new ArrayList<>();
        for (Employee user : userList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }
}
