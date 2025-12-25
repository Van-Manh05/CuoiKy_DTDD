package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Date;

public class RutTienActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtAmount;
    private TextView tvCurrentBalance;
    private MaterialButton btnConfirm;

    private FirebaseFirestore db;
    private String uid;
    private String walletId, walletName;
    private double currentBalance = 0;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rut_tien);

        // Nhận dữ liệu từ ChiTietViActivity
        walletId = getIntent().getStringExtra("walletId");
        walletName = getIntent().getStringExtra("walletName");
        currentBalance = getIntent().getDoubleExtra("walletBalance", 0);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtAmount = findViewById(R.id.edtAmount);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        btnConfirm = findViewById(R.id.btnConfirm);

        tvCurrentBalance.setText("Số dư khả dụng: " + formatter.format(currentBalance) + " đ");
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> handleWithdraw());
    }

    private void handleWithdraw() {
        String amountStr = edtAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double tempAmount = 0;
        try {
            tempAmount = Double.parseDouble(amountStr.replace(",", "").replace(".", ""));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        final double finalAmount = tempAmount;

        // KIỂM TRA SỐ DƯ
        if (finalAmount > currentBalance) {
            Toast.makeText(this, "Số dư không đủ để rút!", Toast.LENGTH_LONG).show();
            return;
        }

        if (finalAmount <= 0) {
            Toast.makeText(this, "Số tiền rút phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang xử lý...");

        if (uid != null && walletId != null) {
            // 1. Trừ tiền trong Ví (Cộng với số âm)
            db.collection("users").document(uid).collection("wallets")
                    .document(walletId)
                    .update("balance", FieldValue.increment(-finalAmount))
                    .addOnSuccessListener(aVoid -> {
                        // 2. Lưu lịch sử giao dịch
                        saveTransactionHistory(finalAmount);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Xác nhận rút tiền");
                    });
        }
    }

    private void saveTransactionHistory(double amount) {
        // Lưu category là "Rút tiền" hoặc "Chi tiêu" tùy bạn
        GiaoDich gd = new GiaoDich(amount, "Rút tiền", "Rút tiền từ ví " + walletName, new Date(), "CHI");

        db.collection("users").document(uid).collection("transactions")
                .add(gd)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Rút tiền thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}