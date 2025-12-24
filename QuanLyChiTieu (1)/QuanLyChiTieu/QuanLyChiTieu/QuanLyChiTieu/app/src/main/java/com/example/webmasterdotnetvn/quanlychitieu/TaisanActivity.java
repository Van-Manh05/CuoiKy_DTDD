package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TaisanActivity extends AppCompatActivity {

    // Views
    private RecyclerView recyclerView;
    private ViTienAdapter adapter;
    private List<ViTien> danhSachVi;

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    private TextView tvTongTaiSan;
    private TextView tvViCoBanTitle;

    // Khai báo 2 nút chức năng
    private View btnTaoVi;
    private View btnNapTien;

    // Variables
    private boolean isExpanded = true;
    private FirebaseFirestore db;
    private String uid;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taisan);

        // 1. Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // 2. Ánh xạ Views
        initViews();

        // 3. Setup RecyclerView (ĐÃ CẬP NHẬT LISTENER)
        setupRecyclerView();

        // 4. Lấy dữ liệu thật từ Firebase
        loadRealDataFromFirebase();

        // 5. Xử lý sự kiện click
        setupEvents();

        // 6. Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView_viTien);
        tvViCoBanTitle = findViewById(R.id.tvViCoBanTitle);
        tvTongTaiSan = findViewById(R.id.tvTongTaiSan);

        // Ánh xạ 2 nút bấm
        btnTaoVi = findViewById(R.id.btnTaoVi);
        btnNapTien = findViewById(R.id.btnNapTien);

        // Bottom Nav
        bottomNavigationView = findViewById(R.id.bottomNavigationView_taisan);
        fab = findViewById(R.id.fab_taisan);
    }

    private void setupRecyclerView() {
        danhSachVi = new ArrayList<>();

        // --- CẬP NHẬT QUAN TRỌNG: Truyền Listener vào Adapter ---
        adapter = new ViTienAdapter(danhSachVi, new ViTienAdapter.OnWalletClickListener() {
            @Override
            public void onWalletClick(ViTien viTien) {
                // Khi bấm vào 1 dòng ví -> Mở màn hình Chi tiết
                Intent intent = new Intent(TaisanActivity.this, ChiTietViActivity.class);
                intent.putExtra("walletId", viTien.getId());
                intent.putExtra("walletName", viTien.getName());
                intent.putExtra("walletBalance", viTien.getBalance());
                startActivity(intent);
            }
        });

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadRealDataFromFirebase() {
        if (uid == null) return;

        db.collection("users").document(uid).collection("wallets")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        danhSachVi.clear();
                        double totalBalance = 0;

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String name = doc.getString("name");
                            Double balance = doc.getDouble("balance");
                            if (balance == null) balance = 0.0;

                            totalBalance += balance;
                            danhSachVi.add(new ViTien(doc.getId(), name, balance));
                        }

                        adapter.notifyDataSetChanged();
                        if (tvTongTaiSan != null) {
                            tvTongTaiSan.setText(formatter.format(totalBalance) + " đ");
                        }
                    }
                });
    }

    private void setupEvents() {
        // Nút "+ Tạo ví" -> Mở màn hình Quản Lý Ví
        btnTaoVi.setOnClickListener(v -> {
            Intent intent = new Intent(TaisanActivity.this, QuanLyViActivity.class);
            startActivity(intent);
        });

        // Nút "Nạp tiền" -> Mở màn hình Nạp Tiền
        if (btnNapTien != null) {
            btnNapTien.setOnClickListener(v -> {
                Intent intent = new Intent(TaisanActivity.this, NapTienActivity.class);
                startActivity(intent);
            });
        }

        // Ẩn/Hiện danh sách khi bấm tiêu đề
        tvViCoBanTitle.setOnClickListener(v -> {
            if (isExpanded) {
                recyclerView.setVisibility(View.GONE);
                tvViCoBanTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                isExpanded = false;
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvViCoBanTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                isExpanded = true;
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_taisan);

        // FAB ở giữa cũng dùng để Nạp tiền
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(TaisanActivity.this, NapTienActivity.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_taisan) {
                return true;
            } else if (itemId == R.id.nav_lichsu) {
                startActivity(new Intent(getApplicationContext(), LichSuActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_khampha) {
                startActivity(new Intent(getApplicationContext(), KhamPhaActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}