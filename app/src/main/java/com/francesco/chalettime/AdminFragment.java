package com.francesco.chalettime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

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

        List<Recycler_item> user = new ArrayList<>();
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));
        user.add(new Recycler_item("Francesco Sormani", "ore: 3", R.drawable.user_man));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new CustomRecyclerAdapter(requireContext(), user));

    }
}