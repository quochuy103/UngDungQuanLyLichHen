package com.example.quanlylichhen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.adapter.AppointmentAdapter;
import com.example.quanlylichhen.database.DatabaseHelper;
import com.example.quanlylichhen.model.Appointment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalCount;
    private TextView tvReminderCount;
    private ListView lvAppointments;
    private View btnAddAppointment;

    private DatabaseHelper databaseHelper;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvReminderCount = findViewById(R.id.tvReminderCount);
        lvAppointments = findViewById(R.id.lvAppointments);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);

        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
                startActivity(intent);
            }
        });

        lvAppointments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment appointment = (Appointment) adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, AppointmentDetailActivity.class);
                intent.putExtra("appointment_id", appointment.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Appointment> appointmentList = databaseHelper.getAllAppointments();
        int total = databaseHelper.countAll();
        int reminders = databaseHelper.countReminders();

        tvTotalCount.setText(String.valueOf(total));
        tvReminderCount.setText(String.valueOf(reminders));

        if (adapter == null) {
            adapter = new AppointmentAdapter(this, appointmentList);
            lvAppointments.setAdapter(adapter);
        } else {
            adapter.updateData(appointmentList);
        }
    }
}
