package com.francesco.chalettime.ui.home.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.*;
import com.francesco.chalettime.R;
import com.francesco.chalettime.adapter.WorkLogAdapter;
import com.francesco.chalettime.model.WorkLog;
import com.francesco.chalettime.ui.home.viewmodel.UserDetailsViewModel;
import java.util.*;

public class UserDetailsFragment extends Fragment {
    public UserDetailsFragment() { super(R.layout.fragment_user_details); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        UserDetailsViewModel model = new ViewModelProvider(this).get(UserDetailsViewModel.class);
        Bundle args = getArguments();
        int month = args == null ? 0 : args.getInt("selectedMonth");
        TextView name = view.findViewById(R.id.name);
        TextView birthday = view.findViewById(R.id.birthday);
        TextView genderLabel = view.findViewById(R.id.genderLabel);
        name.setText(args == null ? "" : args.getString("userName", ""));
        if (month >= 1 && month <= 12) ((TextView)view.findViewById(R.id.monthLabel)).setText(getResources().getStringArray(R.array.months)[month - 1]);
        RecyclerView list = view.findViewById(R.id.recyclerView);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<WorkLog> rows = new ArrayList<>();
        WorkLogAdapter adapter = new WorkLogAdapter(rows);
        list.setAdapter(adapter);
        model.getUser().observe(getViewLifecycleOwner(), user -> {
            name.setText(getString(R.string.full_name, user.getName(), user.getSurname()));
            birthday.setText(user.getDateOfBirth());
            String gender = user.getGender();
            genderLabel.setText(getString(R.string.gender_format,
                    gender == null || gender.trim().isEmpty() ? getString(R.string.gender_not_specified) : gender));
        });
        model.getWorkLogs().observe(getViewLifecycleOwner(), logs -> {
            rows.clear(); rows.addAll(logs); adapter.notifyDataSetChanged();
            view.findViewById(R.id.empty).setVisibility(logs.isEmpty() && !Boolean.TRUE.equals(model.getLoading().getValue()) ? View.VISIBLE : View.GONE);
        });
        model.getLoading().observe(getViewLifecycleOwner(), loading -> {
            view.findViewById(R.id.progress).setVisibility(loading ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.empty).setVisibility(!loading && rows.isEmpty() ? View.VISIBLE : View.GONE);
        });
        model.getMessages().observe(getViewLifecycleOwner(), event -> {
            String message = event.consume();
            if (message != null) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
        view.findViewById(R.id.imageButton).setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        model.load(args == null ? null : args.getString("userId"), month);
    }
}
