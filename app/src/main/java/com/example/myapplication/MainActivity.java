package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RequestDao requestDao;
    private LinearLayout tableContainer;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        requestDao = new RequestDao(dbHelper);
        tableContainer = findViewById(R.id.tableContainer);

        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                timeHandler.postDelayed(this, 1000);
            }
        };

        findViewById(R.id.btnCreateRequest).setOnClickListener(v ->
                startActivity(new Intent(this, CreateRequestActivity.class)));

        findViewById(R.id.btnStatistics).setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));

        findViewById(R.id.btnPaint).setOnClickListener(v ->
                startActivity(new Intent(this, PaintRequestsActivity.class)));

        findViewById(R.id.btnRepair).setOnClickListener(v ->
                startActivity(new Intent(this, RepairRequestsActivity.class)));

        findViewById(R.id.btnCreateRepair).setOnClickListener(v ->
                startActivity(new Intent(this, CreateRepairRequestActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        timeHandler.post(timeRunnable);
        loadAllRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(timeRunnable);
    }

    private void loadAllRequests() {
        tableContainer.removeAllViews();
        addTableHeader();

        List<String[]> allRequests = requestDao.getAllRequests();
        if (allRequests.isEmpty()) {
            addEmptyRow();
        } else {
            for (String[] req : allRequests) {
                String dateTime = req[5];
                String datePart = dateTime.length() > 5 ? dateTime.substring(0, 5) : dateTime;
                String timePart = dateTime.length() > 10 ? dateTime.substring(11, 16) : "";

                boolean isRepair = "repair".equals(req[6]);
                String modelOrColor = isRepair ? req[3] : req[4];
                String typeLabel = isRepair ? "Рем." : "Покр.";

                // Статус берем из индекса 7
                addTableRow(typeLabel, modelOrColor, datePart, timePart, req[7], req[0]);
            }
        }
    }

    private void addTableHeader() {
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setPadding(4, 8, 4, 8);

        headerRow.addView(createHeaderCell("Тип", 0.7f));
        headerRow.addView(createHeaderCell("Объект", 1.2f));
        headerRow.addView(createHeaderCell("Дата", 0.8f));
        headerRow.addView(createHeaderCell("Время", 0.8f));
        headerRow.addView(createHeaderCell("Статус", 0.8f));
        headerRow.addView(createHeaderCell("Дейст.", 1.2f));

        tableContainer.addView(headerRow);
        addDivider();
    }

    private void addTableRow(String type, String object, String date, String time, String status, String id) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(4, 12, 4, 12);
        row.setGravity(Gravity.CENTER_VERTICAL);

        row.addView(createDataCell(type, 0.7f));
        row.addView(createDataCell(object, 1.2f));
        row.addView(createDataCell(date, 0.8f));
        row.addView(createDataCell(time, 0.8f));
        row.addView(createStatusCell(status, 0.8f));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f);
        actions.setLayoutParams(params);
        actions.setGravity(Gravity.CENTER);

        int iconSize = 60;

        ImageButton edit = new ImageButton(this);
        edit.setImageResource(R.drawable.ic_edit);
        edit.setBackground(null);
        edit.setColorFilter(getResources().getColor(R.color.blue));
        edit.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        edit.setPadding(5,5,5,5);
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditRequestActivity.class);
            intent.putExtra("request_id", id);
            startActivity(intent);
        });
        actions.addView(edit);

        ImageButton check = new ImageButton(this);
        check.setImageResource(R.drawable.ic_check);
        check.setBackground(null);
        check.setColorFilter(getResources().getColor(R.color.green_text));
        check.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        check.setPadding(5,5,5,5);
        check.setOnClickListener(v -> {
            requestDao.updateStatus(id, "выполнено");
            loadAllRequests();
        });
        actions.addView(check);

        ImageButton delete = new ImageButton(this);
        delete.setImageResource(R.drawable.ic_delete);
        delete.setBackground(null);
        delete.setColorFilter(getResources().getColor(R.color.red));
        delete.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        delete.setPadding(5,5,5,5);
        delete.setOnClickListener(v -> {
            long result = requestDao.deleteRequestById(id);
            if (result > 0) {
                Toast.makeText(MainActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
            }
            loadAllRequests();
        });
        actions.addView(delete);

        row.addView(actions);
        tableContainer.addView(row);
        addDivider();
    }

    private void addEmptyRow() {
        LinearLayout row = new LinearLayout(this);
        row.setPadding(16, 16, 16, 16);
        TextView empty = new TextView(this);
        empty.setText("Нет активных заявок");
        empty.setTextColor(getResources().getColor(R.color.white_text));
        empty.setGravity(Gravity.CENTER);
        row.addView(empty);
        tableContainer.addView(row);
    }

    private TextView createHeaderCell(String text, float weight) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.white_text));
        tv.setTextSize(12);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        return tv;
    }

    private TextView createDataCell(String text, float weight) {
        TextView tv = new TextView(this);
        tv.setText(text.isEmpty() ? "—" : text);
        tv.setTextColor(getResources().getColor(R.color.white_text));
        tv.setTextSize(11);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        return tv;
    }

    private TextView createStatusCell(String status, float weight) {
        TextView tv = new TextView(this);
        if ("выполнено".equals(status)) {
            tv.setText("Готов");
            tv.setTextColor(getResources().getColor(R.color.green_text));
        } else {
            tv.setText("Раб.");
            tv.setTextColor(getResources().getColor(R.color.orange_text));
        }
        tv.setTextSize(11);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        return tv;
    }

    private void addDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(getResources().getColor(R.color.gray_divider));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        divider.setLayoutParams(params);
        tableContainer.addView(divider);
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