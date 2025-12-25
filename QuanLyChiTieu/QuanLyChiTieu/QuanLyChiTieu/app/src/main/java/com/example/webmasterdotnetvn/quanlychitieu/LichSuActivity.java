package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LichSuActivity extends AppCompatActivity {

    // Views
    private RecyclerView recyclerView;
    private LichSuAdapter adapter;
    private List<GiaoDich> listGiaoDich;

    private TextView tvDateRange;
    private ImageView btnPrevMonth, btnNextMonth;
    private MaterialButton btnChonVi, btnChonMuc;

    private BottomNavigationView bottomNavigationView;
    // Đã xóa FloatingActionButton fab theo yêu cầu

    // Data & Logic
    private FirebaseFirestore db;
    private String uid;
    private Calendar currentCal;
    private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // --- BIẾN LƯU TRẠNG THÁI LỌC ---
    private String filterWalletName = null; // null = Tất cả ví
    private String filterCategory = null;   // null = Tất cả mục

    private List<String> listWalletNames = new ArrayList<>();
    private List<String> listCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        currentCal = Calendar.getInstance();

        initViews();
        setupRecyclerView();
        setupEvents();

        // Tải dữ liệu cho các bộ lọc
        preloadWallets();
        preloadCategories();

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
        // Đã xóa ánh xạ FAB
    }

    private void setupRecyclerView() {
        listGiaoDich = new ArrayList<>();
        adapter = new LichSuAdapter(listGiaoDich);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupEvents() {
        // Chuyển tháng
        btnPrevMonth.setOnClickListener(v -> {
            currentCal.add(Calendar.MONTH, -1);
            updateDateRangeDisplay();
            loadHistoryData();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCal.add(Calendar.MONTH, 1);
            updateDateRangeDisplay();
            loadHistoryData();
        });

        // Nút Lọc VÍ
        btnChonVi.setOnClickListener(v -> showWalletFilterDialog());

        // Nút Lọc MỤC
        btnChonMuc.setOnClickListener(v -> showCategoryFilterDialog());

        // --- CẬP NHẬT NAVIGATION ---
        bottomNavigationView.setSelectedItemId(R.id.nav_lichsu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(this, TaisanActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_ngansach) {
                // Chuyển về MainActivity (nơi chứa Fragment Ngân sách)
                // Lưu ý: Mặc định sẽ về Tổng quan, cần xử lý thêm ở MainActivity nếu muốn mở thẳng tab Ngân sách
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_lichsu) {
                return true;

            } else if (itemId == R.id.nav_khampha) {
                startActivity(new Intent(this, KhamPhaActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateDateRangeDisplay() {
        Calendar start = (Calendar) currentCal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = (Calendar) currentCal.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        String rangeText = sdfDisplay.format(start.getTime()) + " - " + sdfDisplay.format(end.getTime());
        tvDateRange.setText(rangeText);
    }

    // ================== PHẦN LỌC DỮ LIỆU ==================

    // --- 1. XỬ LÝ LỌC VÍ ---
    private void preloadWallets() {
        if (uid == null) return;
        db.collection("users").document(uid).collection("wallets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listWalletNames.clear();
                    listWalletNames.add("Tất cả ví");
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) listWalletNames.add(name);
                    }
                });
    }

    private void showWalletFilterDialog() {
        if (listWalletNames.isEmpty()) {
            Toast.makeText(this, "Đang tải danh sách ví...", Toast.LENGTH_SHORT).show();
            preloadWallets();
            return;
        }
        String[] walletsArray = listWalletNames.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Chọn ví")
                .setItems(walletsArray, (dialog, which) -> {
                    if (which == 0) {
                        filterWalletName = null;
                        btnChonVi.setText("Tất cả ví");
                    } else {
                        filterWalletName = walletsArray[which];
                        btnChonVi.setText(filterWalletName);
                    }
                    loadHistoryData();
                })
                .show();
    }

    // --- 2. XỬ LÝ LỌC MỤC ---
    private void preloadCategories() {
        if (uid == null) return;
        db.collection("users").document(uid).collection("transactions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> uniqueCategories = new HashSet<>();
                    uniqueCategories.add("Nạp tiền");
                    uniqueCategories.add("Rút tiền");

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String cat = doc.getString("category");
                        if (cat != null && !cat.isEmpty()) {
                            uniqueCategories.add(cat);
                        }
                    }
                    listCategories.clear();
                    listCategories.add("Tất cả mục");
                    listCategories.addAll(uniqueCategories);
                });
    }

    private void showCategoryFilterDialog() {
        if (listCategories.isEmpty()) {
            Toast.makeText(this, "Đang tải danh sách mục...", Toast.LENGTH_SHORT).show();
            preloadCategories();
            return;
        }
        String[] catArray = listCategories.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Chọn mục")
                .setItems(catArray, (dialog, which) -> {
                    if (which == 0) {
                        filterCategory = null;
                        btnChonMuc.setText("Tất cả mục");
                    } else {
                        filterCategory = catArray[which];
                        btnChonMuc.setText(filterCategory);
                    }
                    loadHistoryData();
                })
                .show();
    }

    // --- 3. HÀM TẢI DỮ LIỆU ---
    private void loadHistoryData() {
        if (uid == null) return;

        Calendar start = (Calendar) currentCal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0);

        Calendar end = (Calendar) currentCal.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23); end.set(Calendar.MINUTE, 59); end.set(Calendar.SECOND, 59);

        db.collection("users").document(uid).collection("transactions")
                .whereGreaterThanOrEqualTo("date", start.getTime())
                .whereLessThanOrEqualTo("date", end.getTime())
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        listGiaoDich.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            GiaoDich gd = doc.toObject(GiaoDich.class);
                            if (gd != null) {
                                // Lọc Ví
                                if (filterWalletName != null) {
                                    boolean matchWallet = false;
                                    if (gd.getNote() != null && gd.getNote().contains(filterWalletName)) {
                                        matchWallet = true;
                                    }
                                    if (!matchWallet) continue;
                                }
                                // Lọc Mục
                                if (filterCategory != null) {
                                    if (gd.getCategory() == null || !gd.getCategory().equals(filterCategory)) {
                                        continue;
                                    }
                                }
                                listGiaoDich.add(gd);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}