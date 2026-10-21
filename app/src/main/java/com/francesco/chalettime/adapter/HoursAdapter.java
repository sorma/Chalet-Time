package com.francesco.chalettime.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.francesco.chalettime.R;

import java.util.List;

public class HoursAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> options;
    private int selectedPosition = -1;

    public HoursAdapter(Context context, List<String> options) {
        this.context = context;
        this.options = options;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.hours_item, parent, false);
            holder = new ViewHolder();
            holder.numberText = convertView.findViewById(R.id.numberText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String option = options.get(position);
        holder.numberText.setText(option);

        if (position == selectedPosition) {
            holder.numberText.setTextColor(Color.WHITE);
            holder.numberText.setBackgroundResource(R.drawable.circle_selector);
        } else {
            holder.numberText.setTextColor(Color.BLACK);
            holder.numberText.setBackgroundResource(0);
        }

        return convertView;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView numberText;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}

