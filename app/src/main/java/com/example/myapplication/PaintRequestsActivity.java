package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.database.sqlite.SQLiteDatabase;

public class PaintRequestsActivity extends AppCompatActivity {

    private RequestDao requestDao;
    private LinearLayout layoutPaintRequestsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_requests);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        requestDao = new RequestDao(dbHelper);

        layoutPaintRequestsContainer = findViewById(R.id.layoutPaintRequestsContainer);

        updateDateTime();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(v -> deleteAllRequests());

        loadAllPaintRequests();
    }

    private void loadAllPaintRequests() {
        layoutPaintRequestsContainer.removeAllViews(); // Очищаем старые строки

        List<String[]> requests = requestDao.getRequestsByType("paint");

        if (requests.isEmpty()) {
            // Показываем пустую строку
            LinearLayout emptyRow = new LinearLayout(this);
            emptyRow.setOrientation(LinearLayout.HORIZONTAL);
            emptyRow.setPadding(8, 8, 8, 8);

            TextView emptyCell = new TextView(this);
            emptyCell.setText("Нет заявок");
            emptyCell.setTextColor(getResources().getColor(R.color.white_text));
            emptyCell.setPadding(8, 0, 8, 0);

            emptyRow.addView(emptyCell);
            layoutPaintRequestsContainer.addView(emptyRow);
            return;
        }

        for (String[] request : requests) {
            View requestRow = createRequestRow(request);
            layoutPaintRequestsContainer.addView(requestRow);
        }
    }

    private View createRequestRow(String[] request) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(8, 8, 8, 8);

        // Имя
        TextView tvName = new TextView(this);
        tvName.setText(request[1]);
        tvName.setTextColor(getResources().getColor(R.color.white_text));
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Дата
        TextView tvDate = new TextView(this);
        tvDate.setText(request[5]);
        tvDate.setTextColor(getResources().getColor(R.color.white_text));
        tvDate.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Цвет
        TextView tvColor = new TextView(this);
        tvColor.setText(request[4]);
        tvColor.setTextColor(getResources().getColor(R.color.white_text));
        tvColor.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Статус
        TextView tvStatus = new TextView(this);
        tvStatus.setText(request[6]);
        if ("выполнено".equals(request[6])) {
            tvStatus.setTextColor(getResources().getColor(R.color.green_text));
        } else {
            tvStatus.setTextColor(getResources().getColor(R.color.orange_text));
        }
        tvStatus.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Кнопка "УДАЛИТЬ" — серая с красным текстом, как на скриншоте
        Button btnDelete = new Button(this);
        btnDelete.setText("УДАЛИТЬ");
        btnDelete.setPadding(16, 8, 16, 8);
        btnDelete.setTag(request[0]); // ID заявки

        // Устанавливаем серый фон и красный текст
        btnDelete.setBackgroundTintList(getResources().getColorStateList(R.color.red)); // Серый фон
        btnDelete.setTextColor(getResources().getColor(R.color.white_text)); // Красный текст

        btnDelete.setOnClickListener(v -> {
            String id = (String) v.getTag();
            long result = deleteRequestById(id);
            if (result > 0) {
                Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show();
                loadAllPaintRequests(); // Перезагружаем список
            } else {
                Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
            }
        });

        row.addView(tvName);
        row.addView(tvDate);
        row.addView(tvColor);
        row.addView(tvStatus);
        row.addView(btnDelete); // Добавляем кнопку в строку

        return row;
    }

    private void deleteAllRequests() {
        List<String[]> requests = requestDao.getRequestsByType("paint");
        if (requests.isEmpty()) {
            Toast.makeText(this, "Нет заявок для удаления", Toast.LENGTH_SHORT).show();
            return;
        }

        int deletedCount = 0;
        for (String[] request : requests) {
            long result = deleteRequestById(request[0]);
            if (result > 0) deletedCount++;
        }

        if (deletedCount > 0) {
            Toast.makeText(this, "Удалено заявок: " + deletedCount, Toast.LENGTH_SHORT).show();
            loadAllPaintRequests(); // Перезагружаем список
        } else {
            Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
        }
    }

    private long deleteRequestById(String id) {
        SQLiteDatabase db = requestDao.dbHelper.getWritableDatabase();
        long result = db.delete(DatabaseHelper.TABLE_REQUESTS,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{id});
        db.close();
        return result;
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