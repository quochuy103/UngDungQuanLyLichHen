package com.example.quanlylichhen.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.database.DatabaseHelper;
import com.example.quanlylichhen.model.Appointment;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private ImageView btnBackHeader;
    private EditText edtTitle;
    private EditText edtLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchCompat switchReminder;
    private View btnSave;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        databaseHelper = new DatabaseHelper(this);

        btnBackHeader = findViewById(R.id.btnBackHeader);
        edtTitle = findViewById(R.id.edtTitle);
        edtLocation = findViewById(R.id.edtLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSave = findViewById(R.id.btnSave);

        if (btnBackHeader != null) {
            btnBackHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        timePicker.setIs24HourView(true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointment();
            }
        });
    }

    private void saveAppointment() {
        String title = edtTitle.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Tên lịch hẹn không được để trống");
            edtTitle.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            edtLocation.setError("Địa điểm không được để trống");
            edtLocation.requestFocus();
            return;
        }

        int year = datePicker.getYear();
        int month = datePicker.getMonth(); // 0-based
        int day = datePicker.getDayOfMonth();

        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        // Validate time after current time
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.set(year, month, day, hour, minute, 0);

        if (selectedCal.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, "Thời điểm hẹn (ngày và giờ) phải sau thời điểm hiện tại", Toast.LENGTH_LONG).show();
            return;
        }

        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        boolean reminder = switchReminder.isChecked();

        Appointment appointment = new Appointment(title, location, dateStr, timeStr, reminder);
        long result = databaseHelper.insertAppointment(appointment);

        if (result > 0) {
            Toast.makeText(this, "Thêm lịch hẹn thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu lịch hẹn", Toast.LENGTH_SHORT).show();
        }
    }
}
