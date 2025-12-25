package com.example.webmasterdotnetvn.quanlychitieu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // position 0 là tab đầu tiên, 1 là tab thứ hai
        if (position == 0) {
            return new NhomChiFragment();
        } else {
            return new MucChiFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}