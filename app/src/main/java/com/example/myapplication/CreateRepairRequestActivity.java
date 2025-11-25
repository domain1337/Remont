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

    private EditText etOwnerName, etPhone, etModel, etProblemDescription;
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
        etProblemDescription = findViewById(R.id.etProblemDescription); // Поле описания
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSubmit = findViewById(R.id.btnSubmit);

        requestDao = new RequestDao(new DatabaseHelper(this));

        // Валидация телефона
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
                                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
                        );
                        timePickerDialog.show();
                    } else {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                this,
                                (timeView, hourOfDay, minute) -> {
                                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDateTime.set(Calendar.MINUTE, minute);
                                    selectedDateTime.set(Calendar.SECOND, 0);
                                    selectedDateTimeString = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                            .format(selectedDateTime.getTime());
                                    btnSelectDate.setText("Выбрано: " + selectedDateTimeString);
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

    private void submitRequest() {
        String name = etOwnerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String problemDescription = etProblemDescription.getText().toString().trim(); // Новое поле

        if (name.isEmpty() || phone.isEmpty() || model.isEmpty() || selectedDateTimeString.isEmpty() || problemDescription.isEmpty()) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\+\\d{11}") && !phone.matches("\\d{11}")) {
            Toast.makeText(this, "Неверный формат номера телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.startsWith("+")) {
            phone = "+" + phone;
        }

        // Передаем описание проблемы в 7-й аргумент
        long id = requestDao.addRequest(name, phone, model, "", selectedDateTimeString, "repair", problemDescription);

        if (id != -1) {
            Toast.makeText(this, "Заявка на ремонт создана!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}