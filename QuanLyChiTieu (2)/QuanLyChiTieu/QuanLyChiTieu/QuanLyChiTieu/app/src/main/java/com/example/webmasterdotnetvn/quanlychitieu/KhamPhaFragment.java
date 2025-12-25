package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class KhamPhaFragment extends Fragment {

    // Khai báo các biến View
    private CardView btnSettingCategory;
    private TextView tvEmail;

    // Constructor rỗng bắt buộc
    public KhamPhaFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Gắn layout XML vào Fragment
        View view = inflater.inflate(R.layout.fragment_kham_pha, container, false);


        initViews(view);

        // 3. Thiết lập dữ liệu (Giả lập hiển thị email giống ảnh)
        setupData();

        // 4. Xử lý sự kiện click
        setupEvents();

        return view;
    }

    private void initViews(View view) {
        btnSettingCategory = view.findViewById(R.id.btn_setting_category);
        // Giả sử trong XML bạn đặt ID cho TextView hiển thị email là tv_user_email
        // Nếu chưa có ID trong XML thì bạn có thể bỏ qua dòng này
        tvEmail = view.findViewById(R.id.tv_user_email);
    }

    private void setupData() {
        if (tvEmail != null) {
            tvEmail.setText("taiho.310805@gmail.com");
        }
    }

    private void setupEvents() {
        // Sự kiện khi bấm vào "Cài đặt danh mục thu chi"
        btnSettingCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển sang màn hình CategorySettingsActivity
                Intent intent = new Intent(getActivity(), CategorySettingsActivity.class);
                startActivity(intent);
            }
        });

        // (Tùy chọn) Sự kiện khi bấm vào Avatar hoặc các mục khác nếu cần
        // ...

    }

}