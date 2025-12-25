package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Khai báo biến View
    // Lưu ý: Chúng ta sẽ dùng ô nhập SĐT cũ để nhập Email
    EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvLogin;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Ánh xạ View
        edtFullName = findViewById(R.id.edtFullNameRegister);
        // Map ID edtPhoneRegister sang biến edtEmail (vì Firebase dùng Email)
        edtEmail = findViewById(R.id.edtPhoneRegister);
        edtPassword = findViewById(R.id.edtPasswordRegister);
        edtConfirmPassword = findViewById(R.id.edtConfirmPasswordRegister);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // 3. Xử lý sự kiện Đăng Ký
        btnRegister.setOnClickListener(v -> handleRegister());

        // 4. Quay lại màn hình đăng nhập
        tvLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // --- VALIDATE (KIỂM TRA) DỮ LIỆU ---
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- BẮT ĐẦU ĐĂNG KÝ VỚI FIREBASE ---
        Toast.makeText(this, "Đang xử lý đăng ký...", Toast.LENGTH_SHORT).show();
        btnRegister.setEnabled(false); // Khóa nút để tránh bấm nhiều lần

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Bước 1: Tạo tài khoản Auth 
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Bước 2: Lưu thêm thông tin (Họ tên) vào Firestore Database
                        // Cấu trúc: users -> {uid} -> {name: ..., email: ...}
                        saveUserToFirestore(user.getUid(), fullName, email);

                    } else {
                        // Đăng ký thất bại (Email trùng, sai định dạng...)
                        btnRegister.setEnabled(true);
                        String error = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(RegisterActivity.this, "Đăng ký lỗi: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, String fullName, String email) {
        // Tạo gói dữ liệu
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("email", email);
        // Có thể thêm các trường khác như: phone, avatar...

        db.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // Bước 3: Lưu Database thành công -> Chuyển màn hình
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    // Xóa lịch sử để không back lại được trang đăng ký
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "Lỗi lưu tên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}