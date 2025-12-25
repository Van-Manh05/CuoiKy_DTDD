package com.example.webmasterdotnetvn.quanlychitieu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuanLyViActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddWallet;
    private ImageView btnBack;

    private ViTienAdapter adapter;
    private List<ViTien> listVi;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_vi);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews();
        setupRecyclerView();
        loadWallets();
        setupEvents();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView_quanLyVi);
        fabAddWallet = findViewById(R.id.fabAddWallet);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        listVi = new ArrayList<>();

        // Cấu hình Adapter với cả 2 sự kiện: Click và LongClick
        adapter = new ViTienAdapter(listVi, new ViTienAdapter.OnWalletClickListener() {
            @Override
            public void onWalletClick(ViTien viTien) {
                // Click thường: Mở chi tiết ví (giống TaisanActivity)
                Intent intent = new Intent(QuanLyViActivity.this, ChiTietViActivity.class);
                intent.putExtra("walletId", viTien.getId());
                intent.putExtra("walletName", viTien.getName());
                intent.putExtra("walletBalance", viTien.getBalance());
                startActivity(intent);
            }

            @Override
            public void onWalletLongClick(ViTien viTien) {
                // Nhấn giữ: Hiện hộp thoại xác nhận xóa
                showDeleteConfirmDialog(viTien);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadWallets() {
        if (uid == null) return;

        db.collection("users").document(uid).collection("wallets")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        listVi.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String name = doc.getString("name");
                            Double balance = doc.getDouble("balance");
                            if (balance == null) balance = 0.0;

                            listVi.add(new ViTien(doc.getId(), name, balance));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        // Nút FAB ở góc dưới phải -> Mở màn hình tạo ví mới
        fabAddWallet.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyViActivity.this, CreateWalletActivity.class);
            startActivity(intent);
        });
    }

    // Hộp thoại xác nhận xóa
    private void showDeleteConfirmDialog(ViTien viTien) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa ví?")
                .setMessage("Bạn có chắc muốn xóa ví '" + viTien.getName() + "' không? Dữ liệu không thể phục hồi.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteWallet(viTien))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteWallet(ViTien viTien) {
        if (uid == null) return;

        db.collection("users").document(uid).collection("wallets")
                .document(viTien.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa ví thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xóa ví: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}