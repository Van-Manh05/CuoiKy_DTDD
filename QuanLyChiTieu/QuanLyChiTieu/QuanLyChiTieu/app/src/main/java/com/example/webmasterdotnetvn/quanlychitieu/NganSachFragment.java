package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NganSachFragment extends Fragment {

    private ImageView btnAddBudget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ngan_sach, container, false);

        btnAddBudget = view.findViewById(R.id.btnAddBudget);

        btnAddBudget.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng Thêm ngân sách đang phát triển", Toast.LENGTH_SHORT).show();
            // Sau này bạn có thể tạo Activity mới để nhập ngân sách và gọi startActivity ở đây
        });

        return view;
    }
}