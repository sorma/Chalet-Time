package com.francesco.chalettime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder> {

    private final Context context;
    public List<Recycler_item> userList;

    public CustomRecyclerAdapter(Context context, List<Recycler_item> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recycler_item user = userList.get(position);
        holder.hourView.setText(user.getHours());
        holder.nameView.setText(user.getName());
        holder.imageView.setImageResource(user.getImage());
        holder.birthadayView.setText(user.getBirthday());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Recycler_item> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView, hourView, birthadayView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameView = itemView.findViewById(R.id.name);
            hourView = itemView.findViewById(R.id.hour);
            birthadayView = itemView.findViewById(R.id.birthday);
        }
    }
}
