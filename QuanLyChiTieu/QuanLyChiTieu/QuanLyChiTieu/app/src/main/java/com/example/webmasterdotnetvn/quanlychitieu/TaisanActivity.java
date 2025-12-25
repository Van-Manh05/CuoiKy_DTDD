package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
// import android.widget.Toast; // Mở comment nếu muốn hiện thông báo khi nhấn giữ

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

    private RecyclerView recyclerView;
    private ViTienAdapter adapter;
    private List<ViTien> danhSachVi;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private TextView tvTongTaiSan, tvViCoBanTitle;
    private View btnTaoVi, btnNapTien;

    private boolean isExpanded = true;
    private FirebaseFirestore db;
    private String uid;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taisan);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews();
        setupRecyclerView();
        loadRealDataFromFirebase();
        setupEvents();
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView_viTien);
        tvViCoBanTitle = findViewById(R.id.tvViCoBanTitle);
        tvTongTaiSan = findViewById(R.id.tvTongTaiSan);
        btnTaoVi = findViewById(R.id.btnTaoVi);
        btnNapTien = findViewById(R.id.btnNapTien);
        bottomNavigationView = findViewById(R.id.bottomNavigationView_taisan);
        fab = findViewById(R.id.fab_taisan);
    }

    private void setupRecyclerView() {
        danhSachVi = new ArrayList<>();

        // CẬP NHẬT: Thêm override cho onWalletLongClick để khớp với Adapter mới
        adapter = new ViTienAdapter(danhSachVi, new ViTienAdapter.OnWalletClickListener() {
            @Override
            public void onWalletClick(ViTien viTien) {
                // Click thường: Mở chi tiết ví
                Intent intent = new Intent(TaisanActivity.this, ChiTietViActivity.class);
                intent.putExtra("walletId", viTien.getId());
                intent.putExtra("walletName", viTien.getName());
                intent.putExtra("walletBalance", viTien.getBalance());
                startActivity(intent);
            }

            @Override
            public void onWalletLongClick(ViTien viTien) {
                // Click giữ: Ở màn hình Tổng quan tài sản, ta thường KHÔNG cho phép xóa nhanh.
                // Việc xóa nên thực hiện trong màn hình "Quản lý ví" để an toàn.
                // Vì vậy hàm này để trống hoặc hiện thông báo nhỏ.

                // Toast.makeText(TaisanActivity.this, "Vào 'Quản lý ví' để chỉnh sửa hoặc xóa", Toast.LENGTH_SHORT).show();
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
        if (btnTaoVi != null) {
            btnTaoVi.setOnClickListener(v -> startActivity(new Intent(this, QuanLyViActivity.class)));
        }
        if (btnNapTien != null) {
            btnNapTien.setOnClickListener(v -> startActivity(new Intent(this, NapTienActivity.class)));
        }
        if (tvViCoBanTitle != null) {
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
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_taisan);
        fab.setOnClickListener(v -> startActivity(new Intent(this, NapTienActivity.class)));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_taisan) {
                return true;
            } else if (itemId == R.id.nav_lichsu) {
                startActivity(new Intent(this, LichSuActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_khampha) {
                startActivity(new Intent(this, KhamPhaActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });
    }
}