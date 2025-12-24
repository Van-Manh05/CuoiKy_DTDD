package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.widget.ImageView; // Cần import ImageView cho nút Back mới

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CategorySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_settings);

        // --- 1. SETUP NÚT BACK TÙY CHỈNH (ImageView) ---
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            // Bắt sự kiện click vào nút Back mới
            btnBack.setOnClickListener(v -> finish());
        }

        // 2. Setup TabLayout & ViewPager (Giữ nguyên)
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        CategoryPagerAdapter adapter = new CategoryPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 3. Gắn tên cho Tab (Giữ nguyên)
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Danh mục chi");
            } else {
                tab.setText("Danh mục thu");
            }
        }).attach();
    }
}