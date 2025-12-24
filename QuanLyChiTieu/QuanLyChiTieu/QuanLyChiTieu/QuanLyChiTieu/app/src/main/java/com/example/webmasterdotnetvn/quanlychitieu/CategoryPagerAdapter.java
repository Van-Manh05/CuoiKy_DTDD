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
        // Truyền tham số type vào Fragment (0: Chi, 1: Thu)
        return CategoryListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2; // Chỉ có 2 tab
    }
}