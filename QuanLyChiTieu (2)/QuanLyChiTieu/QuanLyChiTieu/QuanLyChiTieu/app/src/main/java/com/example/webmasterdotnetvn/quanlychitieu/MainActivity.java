package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();


        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 3. Xử lý click BottomNav (Có chốt chặn đăng nhập)
        setupBottomNavigation();

        // 4. Xử lý FAB -> Mở màn hình Thêm Giao Dịch (Có chốt chặn đăng nhập)
        fab.setOnClickListener(v -> {
            // --- CHỐT CHẶN 1: Kiểm tra đăng nhập ---
            if (!isUserLoggedIn()) {
                showLoginDialog();
                return; // Dừng lại, không cho mở màn hình thêm
            }

            Intent intent = new Intent(MainActivity.this, ThemGiaoDichActivity.class);
            startActivity(intent);
        });

        // 5. Mặc định load màn hình Tổng quan khi mở app
        if (savedInstanceState == null) {
            loadFragment(new TongQuanFragment());
        }

        // =========================================================================
        // KHU VỰC TẠO DỮ LIỆU GIẢ (CHỈ CHẠY 1 LẦN RỒI COMMENT LẠI)
        // =========================================================================
        //taoDuLieuDaDang();
    }

    // --- HÀM KIỂM TRA ĐĂNG NHẬP (Trả về True/False) ---
    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    // --- HÀM HIỆN HỘP THOẠI BẮT BUỘC ĐĂNG NHẬP ---
    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để sử dụng tính năng này.")
                .setPositiveButton("Đăng nhập ngay", (dialog, which) -> {
                    // Chuyển sang màn hình Login
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Đóng MainActivity để không quay lại được
                })
                .setNegativeButton("Để sau", null) // Đóng hộp thoại
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {

            // --- CHỐT CHẶN 2: Kiểm tra đăng nhập khi chuyển Tab ---
            if (!isUserLoggedIn()) {
                showLoginDialog();
                return false; // Trả về false để KHÔNG cho phép chuyển Tab
            }

            int itemId = item.getItemId();

            if (itemId == R.id.nav_tongquan) {
                loadFragment(new TongQuanFragment());

            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(MainActivity.this, TaisanActivity.class));
                overridePendingTransition(0, 0);

            } else if (itemId == R.id.nav_lichsu) {
                startActivity(new Intent(MainActivity.this, LichSuActivity.class));
                overridePendingTransition(0, 0);

            } else if (itemId == R.id.nav_khampha) {
                startActivity(new Intent(MainActivity.this, KhamPhaActivity.class));
                overridePendingTransition(0, 0);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fragment_container, fragment);
        ft.commit();
    }

    // ====================================================================
    // HÀM TẠO DỮ LIỆU ĐA DẠNG (GIỮ NGUYÊN ĐỂ BẠN TEST NẾU CẦN)
    // ====================================================================
    private void taoDuLieuDaDang() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            Toast.makeText(this, "Chưa đăng nhập, không thể tạo data!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<GiaoDich> listData = new ArrayList<>();
        listData.add(new GiaoDich(50000, "Ăn uống", "Cơm trưa văn phòng", taoNgayLui(0), "CHI"));
        listData.add(new GiaoDich(15000, "Di chuyển", "Gửi xe", taoNgayLui(0), "CHI"));
        listData.add(new GiaoDich(450000, "Mua sắm", "Mua áo phông (Tuần trước)", taoNgayLui(10), "CHI"));
        listData.add(new GiaoDich(3500000, "Nhà cửa", "Tiền nhà tháng trước", taoNgayLui(40), "CHI"));
        listData.add(new GiaoDich(15000000, "Lương", "Lương tháng trước", taoNgayLui(40), "THU"));

        int count = 0;
        for (GiaoDich gd : listData) {
            db.collection("users").document(uid).collection("transactions").add(gd);
            count++;
        }
        Toast.makeText(this, "Đã thêm dữ liệu mẫu!", Toast.LENGTH_LONG).show();
    }

    private Date taoNgayLui(int soNgayLui) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -soNgayLui);
        return cal.getTime();
    }
}