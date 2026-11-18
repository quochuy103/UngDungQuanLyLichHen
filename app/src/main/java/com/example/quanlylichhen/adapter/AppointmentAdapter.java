package com.example.quanlylichhen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.model.Appointment;

import java.util.List;

public class AppointmentAdapter extends BaseAdapter {

    private Context context;
    private List<Appointment> appointmentList;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    public void updateData(List<Appointment> newList) {
        this.appointmentList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return appointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return appointmentList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvLocation = convertView.findViewById(R.id.tvLocation);
            holder.tvDateTimeBadge = convertView.findViewById(R.id.tvDateTimeBadge);
            holder.tvReminderBadge = convertView.findViewById(R.id.tvReminderBadge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Appointment a = appointmentList.get(position);

        holder.tvTitle.setText(a.getTitle());
        holder.tvLocation.setText("Địa điểm: " + a.getLocation());

        // Format yyyy-MM-dd -> dd/MM/yyyy
        String formattedDate = formatDate(a.getDate());
        holder.tvDateTimeBadge.setText(formattedDate + " - " + a.getTime());

        if (a.isReminder()) {
            holder.tvReminderBadge.setText("Có nhắc");
            holder.tvReminderBadge.setBackgroundResource(R.drawable.bg_badge_orange);
            holder.tvReminderBadge.setTextColor(Color.parseColor("#E65100"));
        } else {
            holder.tvReminderBadge.setText("Không nhắc");
            holder.tvReminderBadge.setBackgroundResource(R.drawable.bg_badge_gray);
            holder.tvReminderBadge.setTextColor(Color.parseColor("#616161"));
        }

        return convertView;
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || !dateStr.contains("-")) {
            return dateStr;
        }
        String[] parts = dateStr.split("-");
        if (parts.length == 3) {
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }
        return dateStr;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvLocation;
        TextView tvDateTimeBadge;
        TextView tvReminderBadge;
    }
}
