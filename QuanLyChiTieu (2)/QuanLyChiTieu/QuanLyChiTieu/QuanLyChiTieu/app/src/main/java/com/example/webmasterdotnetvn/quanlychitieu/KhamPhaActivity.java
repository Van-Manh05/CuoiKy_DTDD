package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class KhamPhaActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private LinearLayout btnCategorySetting, btnWalletSetting, btnChangePassword, btnLogout;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kham_pha);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        loadUserInfo();
        setupEvents();
        setupBottomNav();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnCategorySetting = findViewById(R.id.btnCategorySetting);
        btnWalletSetting = findViewById(R.id.btnWalletSetting);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvUserEmail.setText(currentUser.getEmail());
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String fullName = doc.getString("fullName");
                                tvUserName.setText(fullName != null ? fullName : "Người dùng");
                            }
                        }
                    });
        }
    }

    private void setupEvents() {
        // 1. Cài đặt danh mục
        btnCategorySetting.setOnClickListener(v -> {
            Intent intent = new Intent(KhamPhaActivity.this, CategorySettingsActivity.class);
            startActivity(intent);
        });

        // 2. Quản lý Ví (MỚI)
        btnWalletSetting.setOnClickListener(v -> {
            Intent intent = new Intent(KhamPhaActivity.this, QuanLyViActivity.class);
            startActivity(intent);
        });

        // 3. Đổi mật khẩu (MỚI)
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // 4. Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    // --- CHỨC NĂNG ĐỔI MẬT KHẨU ---
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu");

        // Tạo layout chứa 2 ô nhập
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText edtOldPass = new EditText(this);
        edtOldPass.setHint("Mật khẩu hiện tại");
        edtOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(edtOldPass);

        final EditText edtNewPass = new EditText(this);
        edtNewPass.setHint("Mật khẩu mới (tối thiểu 6 ký tự)");
        edtNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(edtNewPass);

        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String oldPass = edtOldPass.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            changePasswordFirebase(oldPass, newPass);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void changePasswordFirebase(String oldPass, String newPass) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !TextUtils.isEmpty(oldPass) && !TextUtils.isEmpty(newPass)) {
            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cần xác thực lại trước khi đổi mật khẩu
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Lỗi: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }
    // --------------------------------

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(KhamPhaActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_khampha);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(getApplicationContext(), TaisanActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_lichsu) {
                startActivity(new Intent(getApplicationContext(), LichSuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_khampha) {
                return true;
            }
            return false;
        });
    }
}