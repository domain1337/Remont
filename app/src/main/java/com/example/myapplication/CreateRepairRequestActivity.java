package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        // Валидация номера телефона
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();

                if (input.startsWith("+")) {
                    // Проверяем, что после + только цифры
                    String digits = input.substring(1);
                    if (!digits.matches("\\d*")) {
                        // Удаляем недопустимые символы
                        String cleaned = digits.replaceAll("[^\\d]", "");
                        etPhone.removeTextChangedListener(this);
                        etPhone.setText("+" + cleaned);
                        etPhone.setSelection(etPhone.getText().length());
                        etPhone.addTextChangedListener(this);
                    }
                } else if (!input.isEmpty() && !input.matches("\\d*")) {
                    // Если строка не начинается с + и содержит буквы
                    String cleaned = input.replaceAll("[^\\d]", "");
                    etPhone.removeTextChangedListener(this);
                    etPhone.setText(cleaned);
                    etPhone.setSelection(etPhone.getText().length());
                    etPhone.addTextChangedListener(this);
                }

                // Ограничение длины до 12 символов
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

        btnSelectDate.setOnClickListener(v -> showDateTimePicker());

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Проверка: если сегодня — ограничить время
                    if (selectedDateTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            selectedDateTime.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                            selectedDateTime.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                this,
                                (timeView, hourOfDay, minute) -> {
                                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDateTime.set(Calendar.MINUTE, minute);
                                    selectedDateTime.set(Calendar.SECOND, 0);

                                    if (selectedDateTime.before(now)) {
                                        Toast.makeText(this, "Нельзя выбрать прошлое время", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    selectedDateTimeString = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                            .format(selectedDateTime.getTime());
                                    btnSelectDate.setText("Выбрано: " + selectedDateTimeString);
                                },
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();

                    } else {
                        // Если дата в будущем — любое время
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                this,
                                (timeView, hourOfDay, minute) -> {
                                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDateTime.set(Calendar.MINUTE, minute);
                                    selectedDateTime.set(Calendar.SECOND, 0);

                                    selectedDateTimeString = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                            .format(selectedDateTime.getTime());
                                    btnSelectDate.setText("Выбрано: " + selectedDateTimeString);
                                },
                                0, 0, true
                        );
                        timePickerDialog.show();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // Установка минимальной даты (сегодня)
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    private void submitRequest() {
        String name = etOwnerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String model = etModel.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || model.isEmpty() || selectedDateTimeString.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка формата телефона
        if (!phone.matches("\\+\\d{11}") && !phone.matches("\\d{11}")) {
            Toast.makeText(this, "Неверный формат номера телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        // Если телефон без +, добавляем его
        if (!phone.startsWith("+")) {
            phone = "+" + phone;
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