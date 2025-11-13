package com.example.myapplication;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class EditRequestActivity extends AppCompatActivity {

    private EditText etOwnerName, etPhone, etModel, etColor;
    private TextView tvSelectedDateTime;
    private Button btnSave;
    private RequestDao requestDao;
    private String requestId;

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

        etOwnerName = findViewById(R.id.etOwnerName);
        etPhone = findViewById(R.id.etPhone);
        etModel = findViewById(R.id.etModel);
        etColor = findViewById(R.id.etColor);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        btnSave = findViewById(R.id.btnSave);

        loadRequestData();
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadRequestData() {
        String[] request = requestDao.getRequestById(requestId);
        if (request != null) {
            etOwnerName.setText(request[1]); // name
            etPhone.setText(request[2]);     // phone
            etModel.setText(request[3]);     // model
            etColor.setText(request[4]);     // color
            tvSelectedDateTime.setText(request[5]); // date
        }
    }

    private void saveChanges() {
        String name = etOwnerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String date = tvSelectedDateTime.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = requestDao.updateRequest(requestId, name, phone, model, color, date);
        if (success) {
            Toast.makeText(this, "Заявка обновлена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
        }
    }
}
