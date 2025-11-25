package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class RepairRequestsActivity extends AppCompatActivity {

    private RequestDao requestDao;
    private LinearLayout layoutRequestsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_requests);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        requestDao = new RequestDao(dbHelper);

        layoutRequestsContainer = findViewById(R.id.layoutRequestsContainer);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(v -> deleteAllRequests());

        loadAllRepairRequests();
    }

    private void loadAllRepairRequests() {
        layoutRequestsContainer.removeAllViews();

        List<String[]> requests = requestDao.getRequestsByType("repair");

        if (requests.isEmpty()) {
            LinearLayout emptyRow = new LinearLayout(this);
            emptyRow.setOrientation(LinearLayout.HORIZONTAL);
            emptyRow.setPadding(8, 8, 8, 8);
            TextView emptyCell = new TextView(this);
            emptyCell.setText("Нет заявок");
            emptyCell.setTextColor(getResources().getColor(R.color.white_text));
            emptyRow.addView(emptyCell);
            layoutRequestsContainer.addView(emptyRow);
            return;
        }

        for (String[] request : requests) {
            View requestRow = createRequestRow(request);
            layoutRequestsContainer.addView(requestRow);
        }
    }

    private View createRequestRow(String[] request) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(8, 8, 8, 8);

        TextView tvModel = new TextView(this);
        tvModel.setText(request[3]);
        tvModel.setTextColor(getResources().getColor(R.color.white_text));
        tvModel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        String dateTime = request[5];
        String datePart = dateTime.length() > 10 ? dateTime.substring(0, 10) : dateTime;
        TextView tvDate = new TextView(this);
        tvDate.setText(datePart);
        tvDate.setTextColor(getResources().getColor(R.color.white_text));
        tvDate.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        String timePart = dateTime.length() > 10 ? dateTime.substring(11) : "";
        TextView tvTime = new TextView(this);
        tvTime.setText(timePart);
        tvTime.setTextColor(getResources().getColor(R.color.white_text));
        tvTime.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Статус [7]
        TextView tvStatus = new TextView(this);
        tvStatus.setText(request[7]);
        if ("выполнено".equals(request[7])) {
            tvStatus.setTextColor(getResources().getColor(R.color.green_text));
        } else {
            tvStatus.setTextColor(getResources().getColor(R.color.orange_text));
        }
        tvStatus.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button btnDelete = new Button(this);
        btnDelete.setText("УДАЛИТЬ");
        btnDelete.setPadding(16, 8, 16, 8);
        btnDelete.setTag(request[0]);
        btnDelete.setBackgroundTintList(getResources().getColorStateList(R.color.red));
        btnDelete.setTextColor(getResources().getColor(R.color.white_text));

        btnDelete.setOnClickListener(v -> {
            String id = (String) v.getTag();
            long result = requestDao.deleteRequestById(id);
            if (result > 0) {
                Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show();
                loadAllRepairRequests();
            } else {
                Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
            }
        });

        row.addView(tvModel);
        row.addView(tvDate);
        row.addView(tvTime);
        row.addView(tvStatus);
        row.addView(btnDelete);

        return row;
    }

    private void deleteAllRequests() {
        List<String[]> requests = requestDao.getRequestsByType("repair");
        if (requests.isEmpty()) {
            Toast.makeText(this, "Нет заявок", Toast.LENGTH_SHORT).show();
            return;
        }
        int deletedCount = 0;
        for (String[] request : requests) {
            long result = requestDao.deleteRequestById(request[0]);
            if (result > 0) deletedCount++;
        }
        Toast.makeText(this, "Удалено: " + deletedCount, Toast.LENGTH_SHORT).show();
        loadAllRepairRequests();
    }
}