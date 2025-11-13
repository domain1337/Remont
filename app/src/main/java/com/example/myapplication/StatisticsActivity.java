package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {
    private com.example.myapplication.RequestDao requestDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        com.example.myapplication.DatabaseHelper dbHelper = new com.example.myapplication.DatabaseHelper(this);
        requestDao = new com.example.myapplication.RequestDao(dbHelper);

        updateDateTime();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        List<String[]> all = requestDao.getAllRequests();
        List<String[]> repair = requestDao.getRequestsByType("repair");
        List<String[]> paint = requestDao.getRequestsByType("paint");

        int repairTotal = repair.size();
        int repairInProgress = (int) repair.stream().filter(r -> "в работе".equals(r[6])).count();
        int repairDone = repairTotal - repairInProgress;

        int paintTotal = paint.size();
        int paintInProgress = (int) paint.stream().filter(r -> "в работе".equals(r[6])).count();
        int paintDone = paintTotal - paintInProgress;

        ((TextView) findViewById(R.id.tvRepairTotal)).setText("Общее количество заявок на ремонт: " + repairTotal);
        ((TextView) findViewById(R.id.tvRepairInProgress)).setText("Заявки на ремонт в работе: " + repairInProgress);
        ((TextView) findViewById(R.id.tvRepairDone)).setText("Заявки на ремонт выполнены: " + repairDone);

        ((TextView) findViewById(R.id.tvPaintTotal)).setText("Общее количество заявок на покраску: " + paintTotal);
        ((TextView) findViewById(R.id.tvPaintInProgress)).setText("Заявки на покраску в работе: " + paintInProgress);
        ((TextView) findViewById(R.id.tvPaintDone)).setText("Заявки на покраску выполнены: " + paintDone);
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