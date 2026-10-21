package com.francesco.chalettime.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.francesco.chalettime.model.Employee;
import com.francesco.chalettime.R;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {

    private final Context context;
    private List<Employee> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Employee user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public EmployeeAdapter(Context context, List<Employee> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.getHourView().setText(userList.get(position).getHours());
        holder.getNameView().setText(userList.get(position).getName());
        holder.getImageView().setImageResource(userList.get(position).getImage());
        holder.getBirthadayView().setText(userList.get(position).getBirthday());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(userList.get(position));
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Employee> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView, hourView, birthadayView;

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getHourView() {
            return hourView;
        }

        public TextView getBirthadayView() {
            return birthadayView;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameView = itemView.findViewById(R.id.name);
            hourView = itemView.findViewById(R.id.hour);
            birthadayView = itemView.findViewById(R.id.birthday);

        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

