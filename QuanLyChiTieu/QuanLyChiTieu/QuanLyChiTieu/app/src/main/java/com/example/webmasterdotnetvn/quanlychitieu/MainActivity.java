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

        // Mặc định load màn hình Tổng quan và Hiện FAB
        if (savedInstanceState == null) {
            loadFragment(new TongQuanFragment());
            fab.show();
        }
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
                // Nếu từ Tài sản, Lịch sử... quay về thì mặc định về Tổng quan
                bottomNavigationView.setSelectedItemId(R.id.nav_tongquan);
            }
        }
    }
}