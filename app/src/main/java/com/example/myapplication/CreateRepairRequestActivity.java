package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateRepairRequestActivity extends AppCompatActivity {

    private EditText etOwnerName, etPhone, etModel;
    private Button btnSelectDate, btnSubmit;
    private RequestDao requestDao;
    private Calendar selectedDateTime = Calendar.getInstance();
    private String selectedDateTimeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repair_request);

        etOwnerName = findViewById(R.id.etOwnerName);
        etPhone = findViewById(R.id.etPhone);
        etModel = findViewById(R.id.etModel);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSubmit = findViewById(R.id.btnSubmit);

        requestDao = new RequestDao(new DatabaseHelper(this));

        btnSelectDate.setOnClickListener(v -> showDateTimePicker());

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void showDateTimePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                selectedDateTime.set(Calendar.SECOND, 0);

                selectedDateTimeString = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(selectedDateTime.getTime());

                // Выводим дату на кнопку
                btnSelectDate.setText("Выбрано: " + selectedDateTimeString);

            }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), true)
                    .show();

        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void submitRequest() {
        String name = etOwnerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String model = etModel.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || model.isEmpty() || selectedDateTimeString.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = requestDao.addRequest(name, phone, model, "", selectedDateTimeString, "repair");

        if (id != -1) {
            Toast.makeText(this, "Заявка на ремонт создана!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}