package com.example.webmasterdotnetvn.quanlychitieu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        // Gọi hàm newInstance() static bên CategoryListFragment để tạo Fragment kèm tham số
        return CategoryListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2; // Khai báo số lượng tab là 2 (Chi và Thu)
    }
}