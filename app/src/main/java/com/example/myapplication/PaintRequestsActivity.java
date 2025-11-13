package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
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
    private TextView tvName, tvDate, tvColor, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_requests);

        com.example.myapplication.DatabaseHelper dbHelper = new com.example.myapplication.DatabaseHelper(this);
        requestDao = new RequestDao(dbHelper);

        tvName = findViewById(R.id.tvPaintName);
        tvDate = findViewById(R.id.tvPaintDate);
        tvColor = findViewById(R.id.tvPaintColor);
        tvStatus = findViewById(R.id.tvPaintStatus);

        updateDateTime();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteFirstRequest());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFirstPaintRequest();
    }

    private void loadFirstPaintRequest() {
        List<String[]> requests = requestDao.getRequestsByType("paint");
        if (!requests.isEmpty()) {
            String[] first = requests.get(0);
            tvName.setText(first[1]);
            tvDate.setText(first[5]);
            tvColor.setText(first[4]);
            tvStatus.setText(first[6]);

            if ("выполнено".equals(first[6])) {
                tvStatus.setTextColor(getResources().getColor(R.color.green_text));
            } else {
                tvStatus.setTextColor(getResources().getColor(R.color.orange_text));
            }
        } else {
            tvName.setText("—");
            tvDate.setText("—");
            tvColor.setText("—");
            tvStatus.setText("Нет заявок");
            tvStatus.setTextColor(getResources().getColor(R.color.white_text));
        }
    }

    private void deleteFirstRequest() {
        List<String[]> requests = requestDao.getRequestsByType("paint");
        if (!requests.isEmpty()) {
            String id = requests.get(0)[0]; // первая заявка
            long deleted = deleteRequestById(id);
            if (deleted > 0) {
                Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show();
                loadFirstPaintRequest();
            } else {
                Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Нет заявок для удаления", Toast.LENGTH_SHORT).show();
        }
    }

    private long deleteRequestById(String id) {
        SQLiteDatabase db = requestDao.dbHelper.getWritableDatabase();
        long result = db.delete(com.example.myapplication.DatabaseHelper.TABLE_REQUESTS,
                com.example.myapplication.DatabaseHelper.COLUMN_ID + " = ?", new String[]{id});
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