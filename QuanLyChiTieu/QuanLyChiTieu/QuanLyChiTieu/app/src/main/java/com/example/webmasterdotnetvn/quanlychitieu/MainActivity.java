package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra nếu chưa đăng nhập thì đá về màn hình Login ngay
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupBottomNavigation();
        setupEvents();

        // Mặc định load màn hình Tổng quan
        if (savedInstanceState == null) {
            loadFragment(new TongQuanFragment());
        }
    }

    private void initViews() {
        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupEvents() {
        // Nút FAB -> Mở màn hình Thêm Giao Dịch
        fab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ThemGiaoDichActivity.class));
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tongquan) {
                loadFragment(new TongQuanFragment());
                return true;
            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(MainActivity.this, TaisanActivity.class));
                overridePendingTransition(0, 0); // Tắt hiệu ứng chuyển cảnh để mượt hơn
                return true;
            } else if (itemId == R.id.nav_lichsu) {
                startActivity(new Intent(MainActivity.this, LichSuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_khampha) {
                startActivity(new Intent(MainActivity.this, KhamPhaActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fragment_container, fragment); // ID này phải khớp với layout activity_main.xml
        ft.commit();
    }

    // Khi quay lại từ các Activity khác, đảm bảo Tab Tổng quan được chọn
    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_tongquan);
        }
    }
}