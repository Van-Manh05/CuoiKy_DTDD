package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnLogin;
    TextView tvRegister;

    // SharedPreferences chứa toàn bộ tài khoản đã đăng ký
    SharedPreferences accountPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Khởi tạo SharedPreferences chứa dữ liệu tài khoản
        accountPrefs = getSharedPreferences("UserAccounts", MODE_PRIVATE);

        // 2. Tạo tài khoản mẫu (Có thêm Họ tên và Email để test)
        createDefaultAccounts();

        // 3. Ánh xạ View
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Hiệu ứng
        View root = findViewById(android.R.id.content);
        root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up_slow));

        // 4. Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập số điện thoại và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu
            String savedPassword = accountPrefs.getString(phone, null);

            if (savedPassword != null && savedPassword.equals(password)) {
                // --- ĐĂNG NHẬP THÀNH CÔNG ---

                // A. Lấy thông tin phụ (Tên, Email) từ AccountPrefs
                // Quy ước: Key tên = sđt + "_name", Key email = sđt + "_email"
                String fullName = accountPrefs.getString(phone + "_name", "Người dùng");
                String email = accountPrefs.getString(phone + "_email", "user@email.com");

                // B. Lưu thông tin người dùng hiện tại vào "UserPrefs" (Session)
                // Để các màn hình khác (như Khám Phá) có thể lấy ra dùng
                SharedPreferences sessionPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor sessionEditor = sessionPrefs.edit();
                sessionEditor.putString("FULL_NAME", fullName);
                sessionEditor.putString("EMAIL", email);
                sessionEditor.putString("PHONE", phone);
                sessionEditor.apply(); // Lưu lại

                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // C. Chuyển màn hình
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Số điện thoại hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        // Chuyển sang đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void createDefaultAccounts() {
        SharedPreferences.Editor editor = accountPrefs.edit();

        // Chỉ tạo nếu chưa có dữ liệu
        if (accountPrefs.getAll().isEmpty()) {
            // Tài khoản 1: 0912345678 / 123456
            editor.putString("0912345678", "123456"); // Pass
            editor.putString("0912345678_name", "Nguyễn Văn A"); // Tên
            editor.putString("0912345678_email", "nguyenvana@gmail.com"); // Email

            // Tài khoản 2: 0987654321 / abcdef
            editor.putString("0987654321", "abcdef");
            editor.putString("0987654321_name", "Trần Thị B");
            editor.putString("0987654321_email", "tranthib@yahoo.com");

            editor.apply();
        }
    }
}