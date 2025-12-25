package com.example.webmasterdotnetvn.quanlychitieu;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateWalletActivity extends AppCompatActivity {

    private CardView cardBasic, cardCredit, cardDebt;
    private MaterialButton btnNext;
    private ImageView btnBack;

    // 1: Cơ bản, 2: Tín dụng, 3: Vay nợ
    private int selectedType = 1;

    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews();
        setupEvents();

        // Mặc định chọn Ví cơ bản
        updateSelectionUI();
    }

    private void initViews() {
        cardBasic = findViewById(R.id.cardBasic);
        cardCredit = findViewById(R.id.cardCredit);
        cardDebt = findViewById(R.id.cardDebt);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        // Sự kiện chọn loại ví
        cardBasic.setOnClickListener(v -> {
            selectedType = 1;
            updateSelectionUI();
        });

        cardCredit.setOnClickListener(v -> {
            selectedType = 2;
            updateSelectionUI();
        });

        cardDebt.setOnClickListener(v -> {
            selectedType = 3;
            updateSelectionUI();
        });

        // Nút Tiếp tục -> Hiện Dialog nhập tên và tiền
        btnNext.setOnClickListener(v -> showInputInfoDialog());
    }

    private void updateSelectionUI() {
        // Reset màu nền về trắng
        cardBasic.setCardBackgroundColor(Color.WHITE);
        cardCredit.setCardBackgroundColor(Color.WHITE);
        cardDebt.setCardBackgroundColor(Color.WHITE);

        // Màu xanh nhạt để đánh dấu ví đang chọn
        int highlightColor = Color.parseColor("#E8F5E9");
        String btnText = "";

        switch (selectedType) {
            case 1:
                cardBasic.setCardBackgroundColor(highlightColor);
                btnText = "Chọn Ví Cơ Bản";
                break;
            case 2:
                cardCredit.setCardBackgroundColor(highlightColor);
                btnText = "Chọn Thẻ Tín Dụng";
                break;
            case 3:
                cardDebt.setCardBackgroundColor(highlightColor);
                btnText = "Chọn Ví Vay Nợ";
                break;
        }
        btnNext.setText(btnText);
    }

    private void showInputInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông tin ví mới");

        // --- TẠO GIAO DIỆN NHẬP LIỆU BẰNG CODE (An toàn, không cần XML) ---
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Set padding: Trái, Trên, Phải, Dưới
        layout.setPadding(50, 40, 50, 10);

        // 1. Ô nhập tên ví
        final EditText edtName = new EditText(this);
        edtName.setHint("Tên ví (Ví dụ: Ví tiền mặt)");
        layout.addView(edtName);

        // 2. Ô nhập số dư
        final EditText edtBalance = new EditText(this);
        edtBalance.setHint("Số dư ban đầu");
        edtBalance.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(edtBalance);

        builder.setView(layout);
        // ------------------------------------------------------------------

        builder.setPositiveButton("Tạo ví", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String balanceStr = edtBalance.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Vui lòng nhập tên ví", Toast.LENGTH_SHORT).show();
                return;
            }

            double balance = 0;
            if (!TextUtils.isEmpty(balanceStr)) {
                try {
                    balance = Double.parseDouble(balanceStr);
                } catch (Exception e) {
                    // Bỏ qua nếu nhập sai định dạng số
                }
            }

            createWalletOnFirebase(name, balance);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void createWalletOnFirebase(String name, double balance) {
        if (uid == null) return;

        // Lưu vào Firestore
        ViTien newWallet = new ViTien();
        newWallet.setName(name);
        newWallet.setBalance(balance);
        newWallet.setType(selectedType);

        db.collection("users").document(uid).collection("wallets")
                .add(newWallet)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tạo ví thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình để quay lại danh sách
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}