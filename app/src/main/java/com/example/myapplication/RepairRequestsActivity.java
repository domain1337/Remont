package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class RepairRequestsActivity extends AppCompatActivity {

    private RequestDao requestDao;
    private TextView tvModel, tvDate, tvTime, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_requests);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        requestDao = new RequestDao(dbHelper);

        tvModel = findViewById(R.id.tvRepairModel);
        tvDate = findViewById(R.id.tvRepairDate);
        tvTime = findViewById(R.id.tvRepairTime);
        tvStatus = findViewById(R.id.tvRepairStatus);

        loadFirstRepairRequest();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> deleteFirstRequest());
    }

    private void loadFirstRepairRequest() {
        List<String[]> requests = requestDao.getRequestsByType("repair");
        if (!requests.isEmpty()) {
            String[] first = requests.get(0);
            String dateTime = first[5]; // "dd.MM.yyyy HH:mm"
            String datePart = dateTime.length() > 10 ? dateTime.substring(0, 10) : dateTime;
            String timePart = dateTime.length() > 10 ? dateTime.substring(11) : "";

            tvModel.setText(first[3]); // model
            tvDate.setText(datePart);
            tvTime.setText(timePart);
            tvStatus.setText(first[6]); // status

            if ("выполнено".equals(first[6])) {
                tvStatus.setTextColor(getResources().getColor(R.color.green_text));
            } else {
                tvStatus.setTextColor(getResources().getColor(R.color.orange_text));
            }
        } else {
            tvModel.setText("—");
            tvDate.setText("—");
            tvTime.setText("—");
            tvStatus.setText("Нет заявок");
            tvStatus.setTextColor(getResources().getColor(R.color.white_text));
        }
    }

    private void deleteFirstRequest() {
        List<String[]> requests = requestDao.getRequestsByType("repair");
        if (!requests.isEmpty()) {
            String id = requests.get(0)[0];
            long result = requestDao.deleteRequestById(id);
            if (result > 0) {
                Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show();
                loadFirstRepairRequest();
            } else {
                Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Нет заявок для удаления", Toast.LENGTH_SHORT).show();
        }
    }
}