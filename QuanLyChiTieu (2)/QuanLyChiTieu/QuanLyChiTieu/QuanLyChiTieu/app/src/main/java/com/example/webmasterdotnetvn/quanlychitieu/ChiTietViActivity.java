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
    private double currentBalance = 0;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_vi);

        // Nhận dữ liệu từ TaisanActivity
        walletId = getIntent().getStringExtra("walletId");
        walletName = getIntent().getStringExtra("walletName");
        currentBalance = getIntent().getDoubleExtra("walletBalance", 0);

        db = FirebaseFirestore.getInstance();
        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvWalletNameDetail);
        tvBalance = findViewById(R.id.tvWalletBalanceDetail);
        btnBack = findViewById(R.id.btnBack);
        btnQuickTopUp = findViewById(R.id.btnQuickTopUp);
        btnWithdraw = findViewById(R.id.btnWithdraw);
    }

    private void setupData() {
        tvName.setText(walletName);
        tvBalance.setText(formatter.format(currentBalance) + " đ");

        // (Nâng cao) Tại đây bạn có thể gọi Firestore để load lịch sử giao dịch
        // dựa theo filter note chứa tên ví, hoặc field walletId nếu sau này bạn thêm vào.
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        // Nút Nạp tiền: Chuyển sang NapTienActivity và tự chọn ví này
        btnQuickTopUp.setOnClickListener(v -> {
            Intent intent = new Intent(ChiTietViActivity.this, NapTienActivity.class);
            // Truyền tên ví sang để bên kia tự chọn (Cần update NapTienActivity để xử lý việc này)
            intent.putExtra("preSelectedWalletName", walletName);
            startActivity(intent);
        });

        btnWithdraw.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Rút tiền đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    // Khi quay lại từ màn hình nạp tiền, cần reload lại số dư mới
    @Override
    protected void onResume() {
        super.onResume();
        loadRealTimeBalance();
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
                                tvBalance.setText(formatter.format(bal) + " đ");
                            }
                        }
                    });
        }
    }
}