package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CategorySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_settings);

        // 1. Ánh xạ viu
        // Lưu ý: Đảm bảo id trong file XML là 'btnBack' (ImageView)
        ImageView btnBack = findViewById(R.id.btnBack);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // 2. Xử lý nút Back (Thoát màn hình)
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Cấu hình ViewPager2 với Adapter
        CategoryPagerAdapter adapter = new CategoryPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 4. Kết nối TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Danh mục chi");
            } else {
                tab.setText("Danh mục thu");
            }
        }).attach();
    }
}