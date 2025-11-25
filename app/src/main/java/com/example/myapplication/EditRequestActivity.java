package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditRequestActivity extends AppCompatActivity {

    private EditText etOwnerName, etPhone, etModel, etColor, etProblemDescription;
    private Button btnSave, btnPickDate;
    private RequestDao requestDao;
    private String requestId;

    // Формат для отображения даты на кнопке
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);

        requestId = getIntent().getStringExtra("request_id");
        if (requestId == null) {
            Toast.makeText(this, "Ошибка: ID заявки не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        requestDao = new RequestDao(new DatabaseHelper(this));

        // Инициализация полей
        etOwnerName = findViewById(R.id.etOwnerName);
        etPhone = findViewById(R.id.etPhone);
        etModel = findViewById(R.id.etModel);
        etColor = findViewById(R.id.etColor);
        etProblemDescription = findViewById(R.id.etProblemDescription); // Новое поле
        btnSave = findViewById(R.id.btnSave);
        btnPickDate = findViewById(R.id.btnPickDate);

        loadRequestData();

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Просто обновляем дату, время оставляем 00:00 или текущее,
                    // либо можно добавить TimePickerDialog, если нужно менять и время
                    String formattedDate = dateFormat.format(selectedCalendar.getTime());
                    btnPickDate.setText(formattedDate);
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Запрет на выбор прошедшей даты
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void loadRequestData() {
        String[] request = requestDao.getRequestById(requestId);
        if (request != null) {
            etOwnerName.setText(request[1]);
            etPhone.setText(request[2]);
            etModel.setText(request[3]);
            etColor.setText(request[4]);

            // Загружаем описание проблемы (индекс 8)
            // Проверка на null нужна, если в базе записано null
            if (request.length > 8 && request[8] != null) {
                etProblemDescription.setText(request[8]);
            }

            try {
                // Пытаемся распарсить дату
                if (request[5] != null && !request[5].isEmpty()) {
                    btnPickDate.setText(request[5]);
                }
            } catch (Exception e) {
                btnPickDate.setText("Выбрать дату");
            }
        }
    }

    private void saveChanges() {
        String name = etOwnerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        // Получаем текст описания проблемы
        String problemDescription = etProblemDescription.getText().toString().trim();
        String date = btnPickDate.getText().toString();

        if (date.contains("Выбрать дату")) {
            Toast.makeText(this, "Выберите дату выполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || phone.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // ИСПРАВЛЕННАЯ СТРОКА: передаем 7 аргументов (включая problemDescription)
        boolean success = requestDao.updateRequest(requestId, name, phone, model, color, date, problemDescription);

        if (success) {
            Toast.makeText(this, "Заявка обновлена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
        }
    }
}