package com.francesco.chalettime.ui.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.*;
import com.francesco.chalettime.R;
import com.francesco.chalettime.adapter.EmployeeAdapter;
import com.francesco.chalettime.model.Employee;
import com.francesco.chalettime.ui.home.viewmodel.AdminViewModel;
import com.francesco.chalettime.ui.welcome.WelcomeActivity;
import java.util.*;

public class AdminFragment extends Fragment implements EmployeeAdapter.OnItemClickListener {
    private AdminViewModel viewModel;
    public AdminFragment() { super(R.layout.fragment_admin); }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        RecyclerView list = view.findViewById(R.id.recyclerView);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        EmployeeAdapter adapter = new EmployeeAdapter(requireContext(), new ArrayList<>());
        adapter.setOnItemClickListener(this);
        list.setAdapter(adapter);
        EditText search = view.findViewById(R.id.searchEditText);
        search.setText(viewModel.getQuery());
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { viewModel.filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
        AutoCompleteTextView month = view.findViewById(R.id.month);
        String[] months = getResources().getStringArray(R.array.months);
        month.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, months));
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), selected -> {
            if (selected >= 1 && selected <= 12) month.setText(months[selected - 1], false);
        });
        month.setOnItemClickListener((parent, row, position, id) -> viewModel.selectMonth(position + 1));
        month.setOnClickListener(v -> month.showDropDown());
        viewModel.getEmployeeList().observe(getViewLifecycleOwner(), employees -> {
            adapter.updateList(employees);
            view.findViewById(R.id.empty).setVisibility(employees.isEmpty() && !Boolean.TRUE.equals(viewModel.getLoading().getValue()) ? View.VISIBLE : View.GONE);
        });
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            view.findViewById(R.id.progress).setVisibility(loading ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.empty).setVisibility(!loading && adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        });
        viewModel.getMessages().observe(getViewLifecycleOwner(), event -> {
            String message = event.consume();
            if (message != null) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
        int initialMonth = getArguments() != null ? getArguments().getInt("selectedMonth", Calendar.getInstance().get(Calendar.MONTH) + 1) : Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (viewModel.getSelectedMonth().getValue() == -1) viewModel.selectMonth(initialMonth);
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> {
            viewModel.logout();
            startActivity(new Intent(requireContext(), WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
    }
    @Override public void onItemClick(Employee employee) {
        Bundle args = new Bundle();
        args.putString("userName", employee.getName());
        args.putString("userId", employee.getUserId());
        args.putInt("selectedMonth", viewModel.getSelectedMonth().getValue());
        NavHostFragment.findNavController(this).navigate(R.id.action_adminFragment_to_userDetailsFragment, args);
    }
}
