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
        // Position đổ về thứ tự của Tab (bắt đầu từ 0)
        // 0: Tab "Danh mục chi" -> Truyền 0 vào Fragment
        // 1: Tab "Danh mục thu" -> Truyền 1 vào Fragment

        // Gọi hàm newInstance() static bên CategoryListFragment để tạo Fragment kèm tham số
        return CategoryListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2; // Khai báo số lượng tab là 2 (Chi và Thu)
    }
}