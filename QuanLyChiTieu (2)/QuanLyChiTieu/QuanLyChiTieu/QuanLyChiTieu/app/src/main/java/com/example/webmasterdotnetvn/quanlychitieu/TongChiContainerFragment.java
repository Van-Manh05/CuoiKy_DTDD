// File: TongChiContainerFragment.java
package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TongChiContainerFragment extends Fragment {

    // Đây là code quản lý 2 tab "Nhóm chi" và "Mục chi"
    // Code này TRƯỚC ĐÂY nằm trong MainActivity.java

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter; // Adapter cũ của bạn

    public TongChiContainerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Nạp layout "fragment_tong_chi_container.xml"
        return inflater.inflate(R.layout.fragment_tong_chi_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View từ layout của Fragment này
        tabLayout = view.findViewById(R.id.tabLayout_tongChi);
        viewPager = view.findViewById(R.id.viewPager_tongChi);

        // 1. Setup ViewPager2 và TabLayout

        // Chú ý: Khi dùng Fragment lồng nhau, Adapter phải truyền "this" (Fragment cha)
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setUserInputEnabled(false); // Tắt vuốt

        // 2. Nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Nhóm chi");
                } else {
                    tab.setText("Mục chi");
                }
            }
        }).attach();
    }
}