package com.example.myapplication;
import java.util.Date;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateRequestActivity extends AppCompatActivity {

    private EditText etOwnerName, etPhone, etModel, etColor;
    private Button btnSelectDate, btnSubmit;
    private RequestDao requestDao;
    private String selectedDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        etOwnerName = findViewById(R.id.etOwnerName);
        etPhone = findViewById(R.id.etPhone);
        etModel = findViewById(R.id.etModel);
        etColor = findViewById(R.id.etColor);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        requestDao = new RequestDao(dbHelper);
        updateDateTime();
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnSelectDate.setOnClickListener(v -> showDateTimePicker());

        btnSubmit.setOnClickListener(v -> {
            String name = etOwnerName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String model = etModel.getText().toString().trim();
            String color = etColor.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || model.isEmpty() || selectedDateTime.isEmpty()) {
                Toast.makeText(this, "Заполните все поля и выберите дату", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = requestDao.addRequest(name, phone, model, color, selectedDateTime, "paint");

            if (id != -1) {
                Toast.makeText(this, "Заявка на покраску оформлена!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            now.set(Calendar.YEAR, year);
            now.set(Calendar.MONTH, month);
            now.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                now.set(Calendar.HOUR_OF_DAY, hourOfDay);
                now.set(Calendar.MINUTE, minute);
                now.set(Calendar.SECOND, 0);

                selectedDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(now.getTime());

                btnSelectDate.setText("Выбрано: " + selectedDateTime);

            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
                    .show();

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateTime()
    {
        TextView tvTime = findViewById(R.id.tvTime);
        if (tvTime != null) {
            String time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                    .format(new     Date());
            tvTime.setText(time);
        }
    }
}