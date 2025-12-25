package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Khai báo View
    private EditText edtEmail, edtPassword; // Đổi tên biến thành Email cho đúng bản chất Firebase
    private Button btnLogin;
    private TextView tvRegister;

    // Khai báo Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // Nếu đã đăng nhập rồi -> Chuyển thẳng vào màn hình chính
            goToMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Ánh xạ View
        // Lưu ý: Nếu XML bạn đặt ID là edtPhone, cứ giữ nguyên ID đó nhưng nhập liệu là Email
        edtEmail = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // 3. Hiệu ứng giao diện (Slide up)
        View root = findViewById(android.R.id.content);
        if (root != null) {
            root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up_slow));
        }

        // 4. Xử lý sự kiện nút Đăng Nhập
        btnLogin.setOnClickListener(v -> loginUser());

        // 5. Chuyển sang màn hình Đăng Ký
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập Mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiện thông báo đang xử lý (UX)
        Toast.makeText(this, "Đang đăng nhập...", Toast.LENGTH_SHORT).show();
        btnLogin.setEnabled(false); // Khóa nút để tránh bấm nhiều lần

        // --- GỌI FIREBASE ĐỂ ĐĂNG NHẬP ---
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true); // Mở lại nút
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        // Đăng nhập thất bại (Sai pass, email không tồn tại...)
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(LoginActivity.this, "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Đóng LoginActivity lại để không quay lại được bằng nút Back
    }
}