package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment {

    private RecyclerView recyclerView;
    private int type; // 0: Chi, 1: Thu

    // Phương thức tạo Fragment với tham số
    public static CategoryListFragment newInstance(int type) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putInt("TYPE", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            type = getArguments().getInt("TYPE");
        }

        loadData();
        return view;
    }

    private void loadData() {
        List<Category> list = new ArrayList<>();

        if (type == 0) {
            // --- DỮ LIỆU DANH MỤC CHI (Ảnh 1) ---
            // Lưu ý: Bạn cần có các icon tương ứng trong drawable
            list.add(new Category("Ăn uống", R.drawable.ic_food, "#E0F7FA")); // Xanh nhạt
            list.add(new Category("Thực phẩm", R.drawable.ic_grocery, "#E0F2F1"));
            list.add(new Category("Ăn nhà hàng", R.drawable.ic_restaurant, "#E1F5FE"));
            list.add(new Category("Ăn vặt", R.drawable.ic_snack, "#E3F2FD"));
            list.add(new Category("Cà phê", R.drawable.ic_coffee, "#E8EAF6"));
            list.add(new Category("Nhà cửa", R.drawable.ic_home, "#FCE4EC")); // Hồng nhạt
            list.add(new Category("Thuê nhà", R.drawable.ic_rent, "#F8BBD0"));
            list.add(new Category("Hoá đơn điện", R.drawable.ic_electric, "#F48FB1"));

        } else {
            // --- DỮ LIỆU DANH MỤC THU (Ảnh 2) ---
            list.add(new Category("Lương", R.drawable.ic_salary, "#E0F7FA"));
            list.add(new Category("Thưởng", R.drawable.ic_bonus, "#FCE4EC"));
            list.add(new Category("Được cho, tặng", R.drawable.ic_gift, "#F3E5F5")); // Tím nhạt
            list.add(new Category("Tiền lãi", R.drawable.ic_interest, "#E8F5E9")); // Xanh lá nhạt
            list.add(new Category("Hoàn tiền", R.drawable.ic_refund, "#FFF3E0")); // Cam nhạt
            list.add(new Category("Ưu đãi", R.drawable.ic_discount, "#FFF8E1")); // Vàng nhạt
            list.add(new Category("Bán đồ", R.drawable.ic_sell, "#ECEFF1"));
            list.add(new Category("Thu nhập khác", R.drawable.ic_other, "#E3F2FD"));
        }

        CategoryAdapter adapter = new CategoryAdapter(list);
        recyclerView.setAdapter(adapter);
    }
}