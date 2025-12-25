package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LichSuActivity extends AppCompatActivity {

    // Views
    private RecyclerView recyclerView;
    private LichSuAdapter adapter;
    private List<GiaoDich> listGiaoDich;

    private TextView tvDateRange;
    private ImageView btnPrevMonth, btnNextMonth;
    private MaterialButton btnChonVi, btnChonMuc;

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    // Data & Logic
    private FirebaseFirestore db;
    private String uid;
    private Calendar currentCal; // Dùng để theo dõi tháng hiện tại đang xem
    private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su); // Đảm bảo tên file XML khớp

        // Firebase Init
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // Calendar Init
        currentCal = Calendar.getInstance();

        initViews();
        setupRecyclerView();
        setupEvents();

        // Mặc định load tháng hiện tại
        updateDateRangeDisplay();
        loadHistoryData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvDateRange = findViewById(R.id.tvDateRange);
        btnPrevMonth = findViewById(R.id.arrow_left_date);
        btnNextMonth = findViewById(R.id.arrow_right_date);
        btnChonVi = findViewById(R.id.btnChonVi);
        btnChonMuc = findViewById(R.id.btnChonMuc);

        bottomNavigationView = findViewById(R.id.bottom_navigation_lich_su);
        fab = findViewById(R.id.fab_lich_su);
    }

    private void setupRecyclerView() {
        listGiaoDich = new ArrayList<>();
        adapter = new LichSuAdapter(listGiaoDich);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupEvents() {
        // --- Xử lý chuyển tháng ---
        btnPrevMonth.setOnClickListener(v -> {
            currentCal.add(Calendar.MONTH, -1); // Lùi 1 tháng
            updateDateRangeDisplay();
            loadHistoryData();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCal.add(Calendar.MONTH, 1); // Tiến 1 tháng
            updateDateRangeDisplay();
            loadHistoryData();
        });

        // --- Xử lý Bottom Nav & FAB ---
        bottomNavigationView.setSelectedItemId(R.id.nav_lichsu);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, ThemGiaoDichActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(this, TaisanActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_lichsu) {
                return true;
            } else if (itemId == R.id.nav_khampha) {
                startActivity(new Intent(this, KhamPhaActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });

        // --- Xử lý nút Lọc (Ví dụ) ---
        btnChonVi.setOnClickListener(v -> Toast.makeText(this, "Tính năng lọc Ví đang phát triển", Toast.LENGTH_SHORT).show());
        btnChonMuc.setOnClickListener(v -> Toast.makeText(this, "Tính năng lọc Mục đang phát triển", Toast.LENGTH_SHORT).show());
    }

    // Cập nhật text hiển thị: "01/MM/yyyy - Cuối/MM/yyyy"
    private void updateDateRangeDisplay() {
        // Ngày đầu tháng
        Calendar start = (Calendar) currentCal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);

        // Ngày cuối tháng
        Calendar end = (Calendar) currentCal.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        String rangeText = sdfDisplay.format(start.getTime()) + " - " + sdfDisplay.format(end.getTime());
        tvDateRange.setText(rangeText);
    }

    private void loadHistoryData() {
        if (uid == null) return;

        // 1. Xác định khoảng thời gian (Start & End Date)
        Calendar start = (Calendar) currentCal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0);

        Calendar end = (Calendar) currentCal.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23); end.set(Calendar.MINUTE, 59); end.set(Calendar.SECOND, 59);

        // 2. Query Firestore
        db.collection("users").document(uid).collection("transactions")
                .whereGreaterThanOrEqualTo("date", start.getTime())
                .whereLessThanOrEqualTo("date", end.getTime())
                .orderBy("date", Query.Direction.DESCENDING) // Sắp xếp mới nhất lên đầu
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        listGiaoDich.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            GiaoDich gd = doc.toObject(GiaoDich.class);
                            if (gd != null) {
                                listGiaoDich.add(gd);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}