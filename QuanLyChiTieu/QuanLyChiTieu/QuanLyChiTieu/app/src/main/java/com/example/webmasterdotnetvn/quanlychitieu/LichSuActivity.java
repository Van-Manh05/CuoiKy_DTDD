package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private Calendar currentCal;
    private SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Biến lưu trạng thái lọc
    private String filterWalletName = null; // null = Tất cả ví
    private List<String> listWalletNames = new ArrayList<>(); // Danh sách tên ví để hiển thị trong Dialog

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

        // Tải trước danh sách ví để dùng cho bộ lọc
        preloadWallets();

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

        // Nút Lọc VÍ -> Hiện Dialog chọn
        btnChonVi.setOnClickListener(v -> showWalletFilterDialog());

        // Nút Lọc MỤC (Tương lai)
        btnChonMuc.setOnClickListener(v -> Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show());

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_lichsu);
        fab.setOnClickListener(v -> startActivity(new Intent(this, ThemGiaoDichActivity.class)));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(this, TaisanActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_lichsu) return true;
            else if (itemId == R.id.nav_khampha) {
                startActivity(new Intent(this, KhamPhaActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
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

    // --- LOGIC LỌC VÍ ---

    // 1. Tải danh sách ví về trước
    private void preloadWallets() {
        if (uid == null) return;
        db.collection("users").document(uid).collection("wallets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listWalletNames.clear();
                    listWalletNames.add("Tất cả ví"); // Option mặc định
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) listWalletNames.add(name);
                    }
                });
    }

    // 2. Hiển thị Dialog chọn ví
    private void showWalletFilterDialog() {
        if (listWalletNames.isEmpty()) {
            Toast.makeText(this, "Đang tải danh sách ví...", Toast.LENGTH_SHORT).show();
            preloadWallets();
            return;
        }

        String[] walletsArray = listWalletNames.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ví để lọc");
        builder.setItems(walletsArray, (dialog, which) -> {
            if (which == 0) {
                // Chọn "Tất cả ví"
                filterWalletName = null;
                btnChonVi.setText("Tất cả ví");
            } else {
                // Chọn 1 ví cụ thể
                filterWalletName = walletsArray[which];
                btnChonVi.setText(filterWalletName);
            }
            // Tải lại dữ liệu sau khi chọn
            loadHistoryData();
        });
        builder.show();
    }

    // 3. Tải và Lọc dữ liệu
    private void loadHistoryData() {
        if (uid == null) return;

        Calendar start = (Calendar) currentCal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0);

        Calendar end = (Calendar) currentCal.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23); end.set(Calendar.MINUTE, 59); end.set(Calendar.SECOND, 59);

        // Lấy dữ liệu theo ngày trước
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
                                // --- BỘ LỌC CLIENT-SIDE ---
                                if (filterWalletName != null) {
                                    // Kiểm tra xem giao dịch có liên quan đến ví này không
                                    // 1. Nếu GiaoDich có field walletName
                                    // 2. Hoặc kiểm tra trong Note (Vì code Nạp/Rút cũ lưu tên ví vào Note)
                                    boolean match = false;

                                    // Kiểm tra Note (VD: "Nạp tiền vào ví Tiền mặt")
                                    if (gd.getNote() != null && gd.getNote().contains(filterWalletName)) {
                                        match = true;
                                    }

                                    // Nếu không khớp -> Bỏ qua dòng này (Không add vào list)
                                    if (!match) continue;
                                }

                                listGiaoDich.add(gd);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}