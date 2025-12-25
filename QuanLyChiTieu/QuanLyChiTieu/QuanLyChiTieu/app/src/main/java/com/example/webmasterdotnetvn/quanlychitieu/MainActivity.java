package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupBottomNavigation();
        setupEvents();

        // --- XỬ LÝ LOGIC MỞ TAB NGÂN SÁCH TỪ TRANG KHÁC ---
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    // Hàm xử lý Intent để quyết định mở tab nào
    private void handleIntent(Intent intent) {
        boolean openBudget = intent.getBooleanExtra("open_budget", false);

        if (openBudget) {
            // Nếu có yêu cầu mở Ngân sách
            loadFragment(new NganSachFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_ngansach);
            fab.hide(); // Ẩn nút FAB
        } else {
            // Mặc định mở Tổng quan
            loadFragment(new TongQuanFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_tongquan);
            fab.show(); // Hiện nút FAB
        }
    }

    // Xử lý khi Activity được gọi lại (SingleTop/SingleTask)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void initViews() {
        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupEvents() {
        fab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ThemGiaoDichActivity.class));
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tongquan) {
                loadFragment(new TongQuanFragment());
                fab.show(); // -> HIỆN FAB
                return true;

            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(MainActivity.this, TaisanActivity.class));
                overridePendingTransition(0, 0);
                return true;

            } else if (itemId == R.id.nav_ngansach) {
                loadFragment(new NganSachFragment());
                fab.hide(); // -> ẨN FAB
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
        ft.replace(R.id.main_fragment_container, fragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            // Logic để đảm bảo trạng thái đúng khi quay lại từ Activity khác
            int selectedId = bottomNavigationView.getSelectedItemId();

            if (selectedId == R.id.nav_tongquan) {
                fab.show();
            } else if (selectedId == R.id.nav_ngansach) {
                fab.hide();
            } else {
                // Nếu đang ở các tab ảo (id cũ) mà quay lại thì về Tổng quan
                bottomNavigationView.setSelectedItemId(R.id.nav_tongquan);
                loadFragment(new TongQuanFragment());
                fab.show();
            }
        }
    }
}