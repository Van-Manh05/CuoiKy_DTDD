package com.example.webmasterdotnetvn.quanlychitieu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hiệu ứng chờ 2 giây
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            nextActivity();
        }, 2000);
    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent;
        if (user == null) {
            // Chưa đăng nhập -> Chuyển qua màn hình Đăng nhập
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        } else {
            // Đã đăng nhập -> Chuyển thẳng vào màn hình Chính
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finish(); // Đóng SplashActivity để người dùng không bấm Back quay lại được
    }
}