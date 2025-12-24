package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TaisanActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ViTienAdapter adapter;
    List<ViTien> danhSachVi;

    // Bottom Nav & FAB
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;

    // Các View trong giao diện mới
    TextView tvViCoBanTitle;
    boolean isExpanded = true; // Trạng thái đóng/mở danh sách ví
    View btnTaoVi; // Nút "+ Tạo ví" (Có thể là Button hoặc MaterialButton)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taisan);

        // 1. Ánh xạ Views
        recyclerView = findViewById(R.id.recyclerView_viTien);
        tvViCoBanTitle = findViewById(R.id.tvViCoBanTitle);
        btnTaoVi = findViewById(R.id.btnTaoVi); // Ánh xạ nút Tạo Ví

        // 2. Nạp dữ liệu & Setup RecyclerView
        loadRecyclerViewData();
        adapter = new ViTienAdapter(danhSachVi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // =====================================================
        // 3. XỬ LÝ CLICK CÁC NÚT TRONG TRANG TÀI SẢN
        // =====================================================

        // A. Click nút "+ Tạo ví" -> Chuyển sang màn hình chọn loại ví
        btnTaoVi.setOnClickListener(v -> {
            Intent intent = new Intent(TaisanActivity.this, CreateWalletActivity.class);
            startActivity(intent);
        });

        // B. Click tiêu đề "Ví cơ bản" -> Ẩn/Hiện danh sách
        tvViCoBanTitle.setOnClickListener(v -> {
            if (isExpanded) {
                // Đang mở -> Đóng lại
                recyclerView.setVisibility(View.GONE);
                // Đổi icon mũi tên xuống (ic_arrow_down)
                tvViCoBanTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arr_up, 0);
                isExpanded = false;
            } else {
                // Đang đóng -> Mở ra
                recyclerView.setVisibility(View.VISIBLE);
                // Đổi icon mũi tên lên (ic_arrow_up)
                tvViCoBanTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arr_down, 0);
                isExpanded = true;
            }
        });

        // 4. Xử lý Bottom Nav (Giống MainActivity để chuyển trang)
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView_taisan);
        fab = findViewById(R.id.fab_taisan);

        // Đánh dấu tab "Tài sản" đang được chọn
        bottomNavigationView.setSelectedItemId(R.id.nav_taisan);

        // Nút FAB tròn to ở giữa
        fab.setOnClickListener(v -> {
            Toast.makeText(this, "Bấm nút Thêm (Trang Tài sản)", Toast.LENGTH_SHORT).show();
        });

        // Các tab dưới cùng
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tongquan) {
                Intent intent = new Intent(TaisanActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); 
            } else if (itemId == R.id.nav_lichsu) {
                Intent intent = new Intent(TaisanActivity.this, LichSuActivity.class);
                startActivity(intent);
                finish();
            } else if (itemId == R.id.nav_taisan) {
                return true; // Đang ở đây rồi, không làm gì
            } else if (itemId == R.id.nav_khampha) {
                Intent intent = new Intent(TaisanActivity.this, MainActivity.class);
                intent.putExtra("fragment_to_load", "kham_pha");
                startActivity(intent);
                finish();
            }
            return true;
        });
    }

    private void loadRecyclerViewData() {
        danhSachVi = new ArrayList<>();
        // Dữ liệu giả lập (Icon bạn đã tạo ở bước trước)
        // Lưu ý: Bạn phải chắc chắn đã tạo ic_bank và ic_cash trong res/drawable
        danhSachVi.add(new ViTien(R.drawable.ic_bank, "Tài khoản ngân hàng", 15000000));
        danhSachVi.add(new ViTien(R.drawable.ic_tien, "Tiền mặt", 2000000));
    }
}