package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Quan trọng để dùng View.VISIBLE
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;

    // Các biến View cũ
    LinearLayout btnChonTongChi, btnChonTongThu, btnChonChenhLech;
    MaterialButtonToggleGroup toggleGroupDate;
    TextView tvDateRange;

    // Biến Header (Hộp màu hồng)
    private LinearLayout layoutHomeHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ View
        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // --- LƯU Ý: Kiểm tra ID bên file activity_main.xml ---
        // Nếu bên XML là android:id="@+id/layout_home_header" thì dùng dòng dưới:
        layoutHomeHeader = findViewById(R.id.layoutHeaderHome);
        // Nếu bên XML là android:id="@+id/layoutHeaderHome" thì sửa thành R.id.layoutHeaderHome

        btnChonTongChi = findViewById(R.id.btnChonTongChi);
        btnChonTongThu = findViewById(R.id.btnChonTongThu);
        btnChonChenhLech = findViewById(R.id.btnChonChenhLech);
        toggleGroupDate = findViewById(R.id.toggleGroup_date);
        tvDateRange = findViewById(R.id.tvDateRange);

        // 2. Xử lý click BottomNav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tongquan) {
                showHomeControls(true); // Hiện lại Header
                loadFragment(new TongChiContainerFragment());

            } else if (itemId == R.id.nav_taisan) {
                // Chuyển sang Activity Tài Sản
                startActivity(new Intent(MainActivity.this, TaisanActivity.class));
                overridePendingTransition(0, 0); // Tắt hiệu ứng chuyển cảnh để giống Tab

            } else if (itemId == R.id.nav_lichsu) {
                // Chuyển sang Activity Lịch Sử
                startActivity(new Intent(MainActivity.this, LichSuActivity.class));
                overridePendingTransition(0, 0);

            } else if (itemId == R.id.nav_khampha) {
                showHomeControls(false); // Ẩn Header đi
                loadFragment(new KhamPhaFragment());
            }
            return true;
        });

        // 3. Xử lý FAB (Nút dấu cộng) -> Mở màn hình Thêm Giao Dịch
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ThemGiaoDichActivity.class);
            startActivity(intent);
        });

        // 4. Xử lý click cho 3 nút TAB (Tổng Chi / Tổng Thu / Chênh Lệch)
        btnChonTongChi.setOnClickListener(v -> {
            chonTab(btnChonTongChi);
            loadFragment(new TongChiContainerFragment());
        });
        btnChonTongThu.setOnClickListener(v -> {
            chonTab(btnChonTongThu);
            loadFragment(new TongThuFragment()); // Đảm bảo bạn đã tạo Fragment này
        });
        btnChonChenhLech.setOnClickListener(v -> {
            chonTab(btnChonChenhLech);
            loadFragment(new ChenhLechFragment());
        });

        // 5. Xử lý click "TUẦN/THÁNG/NĂM"
        toggleGroupDate.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnTheoTuan) {
                    tvDateRange.setText("Tuần này");
                } else if (checkedId == R.id.btnTheoThang) {
                    tvDateRange.setText("Tháng này");
                } else if (checkedId == R.id.btnTheoNam) {
                    tvDateRange.setText("Năm này");
                }
            }
        });

        // 6. LOGIC QUAN TRỌNG: Nhận tín hiệu mở Tab từ Activity khác
        if (savedInstanceState == null) {
            // Kiểm tra xem Intent có chứa yêu cầu mở tab Khám Phá không?
            // Key "TARGET_FRAGMENT" phải khớp với code bên LichSuActivity và TaisanActivity
            String targetFragment = getIntent().getStringExtra("TARGET_FRAGMENT");

            if ("KhamPha".equals(targetFragment)) {
                // CÓ -> Mở tab Khám Phá
                bottomNavigationView.setSelectedItemId(R.id.nav_khampha);
                showHomeControls(false);
                loadFragment(new KhamPhaFragment());
            } else {
                // KHÔNG -> Mở trang chủ mặc định
                bottomNavigationView.setSelectedItemId(R.id.nav_tongquan);
                showHomeControls(true);
                loadFragment(new TongChiContainerFragment());
            }
        }
    }

    // --- CÁC HÀM HỖ TRỢ ---

    private void chonTab(LinearLayout selectedTab) {
        btnChonTongChi.setBackgroundResource(android.R.color.transparent);
        btnChonTongThu.setBackgroundResource(android.R.color.transparent);
        btnChonChenhLech.setBackgroundResource(android.R.color.transparent);
        selectedTab.setBackgroundResource(R.drawable.selected_item_border);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        // R.id.main_fragment_container phải khớp ID trong activity_main.xml
        ft.replace(R.id.main_fragment_container, fragment);
        ft.commit();
    }

    // Hàm Ẩn/Hiện Header (Có kiểm tra null để tránh crash)
    private void showHomeControls(boolean isVisible) {
        if (layoutHomeHeader == null) return;

        if (isVisible) {
            layoutHomeHeader.setVisibility(View.VISIBLE);
        } else {
            layoutHomeHeader.setVisibility(View.GONE);
        }
    }
}