package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        // Валидация
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (input.startsWith("+")) {
                    String digits = input.substring(1);
                    if (!digits.matches("\\d*")) {
                        String cleaned = digits.replaceAll("[^\\d]", "");
                        etPhone.removeTextChangedListener(this);
                        etPhone.setText("+" + cleaned);
                        etPhone.setSelection(etPhone.getText().length());
                        etPhone.addTextChangedListener(this);
                    }
                } else if (!input.isEmpty() && !input.matches("\\d*")) {
                    String cleaned = input.replaceAll("[^\\d]", "");
                    etPhone.removeTextChangedListener(this);
                    etPhone.setText(cleaned);
                    etPhone.setSelection(etPhone.getText().length());
                    etPhone.addTextChangedListener(this);
                }
                if (input.length() > 12) {
                    etPhone.removeTextChangedListener(this);
                    etPhone.setText(input.substring(0, 12));
                    etPhone.setSelection(etPhone.getText().length());
                    etPhone.addTextChangedListener(this);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

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

            if (!phone.matches("\\+\\d{11}") && !phone.matches("\\d{11}")) {
                Toast.makeText(this, "Неверный формат номера телефона", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.startsWith("+")) {
                phone = "+" + phone;
            }

            // ПЕРЕДАЕМ ПУСТУЮ СТРОКУ "" ВМЕСТО ОПИСАНИЯ ПРОБЛЕМЫ
            long id = requestDao.addRequest(name, phone, model, color, selectedDateTime, "paint", "");

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    if (selectedDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            selectedDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                            selectedDate.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                this,
                                (timeView, hourOfDay, minute) -> {
                                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDate.set(Calendar.MINUTE, minute);
                                    selectedDate.set(Calendar.SECOND, 0);

                                    if (selectedDate.before(now)) {
                                        Toast.makeText(this, "Нельзя выбрать прошлое время", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    selectedDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                            .format(selectedDate.getTime());
                                    btnSelectDate.setText("Выбрано: " + selectedDateTime);
                                },
                                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
                        );
                        timePickerDialog.show();
                    } else {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                this,
                                (timeView, hourOfDay, minute) -> {
                                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDate.set(Calendar.MINUTE, minute);
                                    selectedDate.set(Calendar.SECOND, 0);
                                    selectedDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                            .format(selectedDate.getTime());
                                    btnSelectDate.setText("Выбрано: " + selectedDateTime);
                                }, 0, 0, true
                        );
                        timePickerDialog.show();
                    }
                },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateDateTime() {
        TextView tvTime = findViewById(R.id.tvTime);
        if (tvTime != null) {
            String time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            tvTime.setText(time);
        }
    }
}