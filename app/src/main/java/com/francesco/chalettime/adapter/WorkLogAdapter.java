package com.francesco.chalettime.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.francesco.chalettime.R;
import com.francesco.chalettime.model.WorkLog;

import java.util.List;

public class WorkLogAdapter extends RecyclerView.Adapter<WorkLogAdapter.WorkLogViewHolder> {

    private final List<WorkLog> workLogs;

    public WorkLogAdapter(List<WorkLog> workLogs) {
        this.workLogs = workLogs;
    }

    @NonNull
    @Override
    public WorkLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.worklog_item, parent, false);
        return new WorkLogViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WorkLogViewHolder holder, int position) {
        holder.getDateTextView().setText(workLogs.get(position).getDate());
        holder.getHoursTextView().setText("Hours: " + workLogs.get(position).getHours());
    }


    public static class WorkLogViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, hoursTextView;

        public WorkLogViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date);
            hoursTextView = itemView.findViewById(R.id.hour);
        }

        public TextView getDateTextView() {
            return dateTextView;
        }

        public TextView getHoursTextView() {
            return hoursTextView;
        }
    }

    @Override
    public int getItemCount() {
        return workLogs.size();
    }
}