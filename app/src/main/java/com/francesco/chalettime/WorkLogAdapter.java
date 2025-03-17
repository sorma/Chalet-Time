package com.francesco.chalettime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkLogAdapter extends RecyclerView.Adapter<WorkLogAdapter.WorkLogViewHolder> {

    private final List<WorkLog> workLogs;

    public WorkLogAdapter(List<WorkLog> workLogs) {
        this.workLogs = workLogs;
    }

    @NonNull
    @Override
    public WorkLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_item, parent, false);
        return new WorkLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkLogViewHolder holder, int position) {
        WorkLog workLog = workLogs.get(position);
        holder.dateTextView.setText(workLog.getDate());
        holder.hoursTextView.setText(String.valueOf("Hours: " + workLog.getHours()));
    }

    @Override
    public int getItemCount() {
        return workLogs.size();
    }

    public static class WorkLogViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, hoursTextView;

        public WorkLogViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date);
            hoursTextView = itemView.findViewById(R.id.hour);
        }
    }
}