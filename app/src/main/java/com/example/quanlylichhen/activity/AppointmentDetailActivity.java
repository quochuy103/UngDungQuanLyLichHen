package com.example.quanlylichhen.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.database.DatabaseHelper;
import com.example.quanlylichhen.model.Appointment;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageView btnBackHeader;
    private TextView tvTitle;
    private TextView tvLocation;
    private TextView tvDateTime;
    private TextView tvReminderState;
    private SwitchCompat switchReminder;
    private View btnBack;
    private View btnDelete;

    private DatabaseHelper databaseHelper;
    private int appointmentId = -1;
    private Appointment currentAppointment;
    private boolean isUserAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        databaseHelper = new DatabaseHelper(this);

        btnBackHeader = findViewById(R.id.btnBackHeader);
        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvReminderState = findViewById(R.id.tvReminderState);
        switchReminder = findViewById(R.id.switchReminder);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        if (btnBackHeader != null) {
            btnBackHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        appointmentId = getIntent().getIntExtra("appointment_id", -1);
        if (appointmentId == -1) {
            Toast.makeText(this, "Lịch hẹn không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDetail();

        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isUserAction) return;

                databaseHelper.updateReminder(appointmentId, isChecked);
                if (tvReminderState != null) {
                    tvReminderState.setText(isChecked ? "Đang bật" : "Đang tắt");
                }
                Toast.makeText(AppointmentDetailActivity.this,
                        isChecked ? "Đã bật nhắc nhở" : "Đã tắt nhắc nhở",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void loadDetail() {
        currentAppointment = databaseHelper.getAppointmentById(appointmentId);
        if (currentAppointment == null) {
            Toast.makeText(this, "Không tìm thấy thông tin lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(currentAppointment.getTitle());
        tvLocation.setText(currentAppointment.getLocation());

        String formattedDate = formatDate(currentAppointment.getDate());
        tvDateTime.setText(formattedDate + " - " + currentAppointment.getTime());

        isUserAction = false;
        switchReminder.setChecked(currentAppointment.isReminder());
        if (tvReminderState != null) {
            tvReminderState.setText(currentAppointment.isReminder() ? "Đang bật" : "Đang tắt");
        }
        isUserAction = true;
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

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch hẹn")
                .setMessage("Bạn có chắc chắn muốn xóa lịch hẹn này không?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleted = databaseHelper.deleteAppointment(appointmentId);
                        if (deleted) {
                            Toast.makeText(AppointmentDetailActivity.this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AppointmentDetailActivity.this, "Xóa lịch hẹn thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
