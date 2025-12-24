package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout; // Import mới
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class KhamPhaActivity extends AppCompatActivity {

    private CardView btnSettingCategory;
    private TextView tvEmail;
    private ImageView imgSettings;

    // Khai báo nút Đăng xuất mới
    private LinearLayout btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kham_pha);

        initViews();
        setupData();
        setupEvents();
        setupBottomNavigation();
    }

    private void initViews() {
        btnSettingCategory = findViewById(R.id.btn_setting_category);
        tvEmail = findViewById(R.id.tv_user_email);
        imgSettings = findViewById(R.id.img_settings);

        // Ánh xạ nút Đăng xuất
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("FULL_NAME", "Người dùng");
        if (tvEmail != null) {
            tvEmail.setText(fullName);
        }
    }

    private void setupEvents() {
        // 1. Chuyển sang Cài đặt danh mục
        if (btnSettingCategory != null) {
            btnSettingCategory.setOnClickListener(v -> {
                Intent intent = new Intent(KhamPhaActivity.this, CategorySettingsActivity.class);
                startActivity(intent);
            });
        }

        // 2. Nút Cài đặt ngôn ngữ
        if (imgSettings != null) {
            imgSettings.setOnClickListener(v -> onSettingsClick(v));
        }

        // 3. XỬ LÝ ĐĂNG XUẤT (Mới thêm)
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        }
    }

    // --- HÀM XÁC NHẬN ĐĂNG XUẤT ---
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // --- HÀM THỰC HIỆN ĐĂNG XUẤT ---
    private void performLogout() {
        // 1. Xóa thông tin phiên đăng nhập (UserPrefs)
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa hết dữ liệu đã lưu tạm
        editor.apply();

        Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

        // 2. Chuyển về màn hình Login và xóa lịch sử Activity
        Intent intent = new Intent(KhamPhaActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack để không back lại được
        startActivity(intent);
        finish();
    }

    // ... (Các hàm xử lý Ngôn ngữ và BottomNav giữ nguyên như cũ) ...

    public void onSettingsClick(View view) {
        showLanguageDialog();
    }

    private void showLanguageDialog() {
        final String[] languages = {"Tiếng Việt", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ngôn ngữ / Select Language");
        builder.setItems(languages, (dialog, which) -> {
            if (which == 0) setLocale("vi");
            else setLocale("en");
        });
        builder.show();
    }

    @SuppressWarnings("deprecation")
    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, dm);
        Intent refresh = new Intent(this, KhamPhaActivity.class);
        startActivity(refresh);
        finish();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_kham_pha);
        FloatingActionButton fab = findViewById(R.id.fab_kham_pha);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_khampha);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_tongquan) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_lichsu) {
                    startActivity(new Intent(getApplicationContext(), LichSuActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_taisan) {
                    startActivity(new Intent(getApplicationContext(), TaisanActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_khampha) {
                    return true;
                }
                return false;
            });
        }
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(KhamPhaActivity.this, ThemGiaoDichActivity.class);
                startActivity(intent);
            });
        }
    }
}