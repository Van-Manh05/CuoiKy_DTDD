package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class ChiTietViActivity extends AppCompatActivity {

    private TextView tvName, tvBalance;
    private ImageView btnBack;
    private MaterialButton btnQuickTopUp, btnWithdraw;

    private FirebaseFirestore db;
    private String walletId, walletName;

    // Đưa biến này ra ngoài để dùng chung cho cả onCreate và loadRealTimeBalance
    private double currentBalance = 0;

    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_vi);

        // Nhận dữ liệu
        walletId = getIntent().getStringExtra("walletId");
        walletName = getIntent().getStringExtra("walletName");

        // Gán vào biến toàn cục
        currentBalance = getIntent().getDoubleExtra("walletBalance", 0);

        db = FirebaseFirestore.getInstance();
        initViews();

        tvName.setText(walletName);
        tvBalance.setText(formatter.format(currentBalance) + " đ");

        setupEvents();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvWalletNameDetail);
        tvBalance = findViewById(R.id.tvWalletBalanceDetail);
        btnBack = findViewById(R.id.btnBack);
        btnQuickTopUp = findViewById(R.id.btnQuickTopUp);
        btnWithdraw = findViewById(R.id.btnWithdraw);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        // 1. Nút NẠP TIỀN -> Chuyển sang NapTienActivity
        btnQuickTopUp.setOnClickListener(v -> {
            Intent intent = new Intent(ChiTietViActivity.this, NapTienActivity.class);
            intent.putExtra("preSelectedWalletName", walletName);
            startActivity(intent);
        });

        // 2. Nút RÚT TIỀN (ĐÃ CẬP NHẬT) -> Chuyển sang RutTienActivity
        btnWithdraw.setOnClickListener(v -> {
            Intent intent = new Intent(ChiTietViActivity.this, RutTienActivity.class);
            intent.putExtra("walletId", walletId);
            intent.putExtra("walletName", walletName);
            // Quan trọng: Truyền số dư mới nhất sang để bên kia kiểm tra
            intent.putExtra("walletBalance", currentBalance);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRealTimeBalance(); // Cập nhật lại số dư khi quay lại từ màn hình Nạp/Rút
    }

    private void loadRealTimeBalance() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null && walletId != null) {
            db.collection("users").document(uid).collection("wallets").document(walletId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Double bal = documentSnapshot.getDouble("balance");
                            if (bal != null) {
                                // Cập nhật biến toàn cục để nút Rút tiền dùng số mới nhất
                                currentBalance = bal;
                                tvBalance.setText(formatter.format(bal) + " đ");
                            }
                        }
                    });
        }
    }
}