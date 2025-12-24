package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFullName, edtPhone, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvLogin;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo SharedPreferences (Database tài khoản)
        sharedPreferences = getSharedPreferences("UserAccounts", MODE_PRIVATE);

        // 1. Ánh xạ View
        edtFullName = findViewById(R.id.edtFullNameRegister);
        edtPhone = findViewById(R.id.edtPhoneRegister);
        edtPassword = findViewById(R.id.edtPasswordRegister);
        edtConfirmPassword = findViewById(R.id.edtConfirmPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // 2. Viết sự kiện Click cho nút "Đăng ký"
        btnRegister.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Kiểm tra rỗng
            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu khớp
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra số điện thoại đã tồn tại chưa
            if (sharedPreferences.contains(phone)) {
                Toast.makeText(RegisterActivity.this, "Số điện thoại này đã được đăng ký", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- A. LƯU TÀI KHOẢN VÀO DATABASE (Để sau này đăng nhập lại) ---
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(phone, password);           // Key: SĐT -> Value: Mật khẩu
            editor.putString(phone + "_name", fullName); // Key: SĐT_name -> Value: Họ tên (QUAN TRỌNG)
            editor.apply();

            // --- B. LƯU PHIÊN ĐĂNG NHẬP HIỆN TẠI (Để hiển thị tên ngay lập tức) ---
            SharedPreferences sessionPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor sessionEditor = sessionPrefs.edit();
            sessionEditor.putString("FULL_NAME", fullName); // Lưu tên để KhamPhaActivity lấy ra
            sessionEditor.putString("PHONE", phone);
            sessionEditor.apply();

            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

            // --- C. CHUYỂN THẲNG VÀO MÀN HÌNH CHÍNH ---
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            // Xóa các màn hình cũ để người dùng không bấm Back quay lại trang đăng ký được
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 3. Sự kiện click cho TextView "Đăng nhập"
        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }
}