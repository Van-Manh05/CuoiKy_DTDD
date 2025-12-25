package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NapTienActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtAmount;
    private Spinner spinnerWallets;
    private MaterialButton btnConfirm;

    private FirebaseFirestore db;
    private String uid;

    // Danh sách lưu ID và Tên ví để xử lý
    private List<String> walletNames;
    private List<String> walletIds;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nap_tien);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews();
        loadWallets();

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleTopUp());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtAmount = findViewById(R.id.edtAmount);
        spinnerWallets = findViewById(R.id.spinnerWallets);
        btnConfirm = findViewById(R.id.btnConfirm);

        walletNames = new ArrayList<>();
        walletIds = new ArrayList<>();

        // Adapter cho Spinner
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, walletNames);
        spinnerWallets.setAdapter(adapter);
    }

    private void loadWallets() {
        if (uid == null) return;

        db.collection("users").document(uid).collection("wallets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    walletNames.clear();
                    walletIds.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) {
                            walletNames.add(name);
                            walletIds.add(doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void handleTopUp() {
        String amountStr = edtAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        if (walletIds.isEmpty()) {
            Toast.makeText(this, "Chưa có ví nào để nạp", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int selectedIndex = spinnerWallets.getSelectedItemPosition();
        String selectedWalletId = walletIds.get(selectedIndex);
        String selectedWalletName = walletNames.get(selectedIndex);

        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang xử lý...");

        // 1. Cập nhật Số dư Ví (Cộng thêm tiền)
        db.collection("users").document(uid).collection("wallets")
                .document(selectedWalletId)
                .update("balance", FieldValue.increment(amount))
                .addOnSuccessListener(aVoid -> {

                    // 2. Lưu lịch sử giao dịch (Để hiện trong thống kê)
                    saveTransactionHistory(amount, selectedWalletName);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi nạp tiền: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnConfirm.setEnabled(true);
                    btnConfirm.setText("Xác nhận nạp tiền");
                });
    }

    private void saveTransactionHistory(double amount, String walletName) {
        // Tạo đối tượng giao dịch loại "THU" (Thu nhập)
        // Category là "Nạp tiền" để dễ phân biệt
        GiaoDich gd = new GiaoDich(amount, "Nạp tiền", "Nạp tiền vào ví " + walletName, new Date(), "THU");

        db.collection("users").document(uid).collection("transactions")
                .add(gd)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Nạp tiền thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình
                });
    }
}