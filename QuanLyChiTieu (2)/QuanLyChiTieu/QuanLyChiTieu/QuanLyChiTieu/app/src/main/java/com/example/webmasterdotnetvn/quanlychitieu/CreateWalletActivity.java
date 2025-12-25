package com.example.webmasterdotnetvn.quanlychitieu; // Giữ đúng package của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // Hoặc com.google.android.material.card.MaterialCardView

import com.google.android.material.card.MaterialCardView;

public class CreateWalletActivity extends AppCompatActivity {

    ImageView btnBack;
    MaterialCardView cardViCoBan, cardTheTinDung, cardViVayNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        // 1. Ánh xạ
        btnBack = findViewById(R.id.btnBack);
        cardViCoBan = findViewById(R.id.cardViCoBan);
        cardTheTinDung = findViewById(R.id.cardTheTinDung);
        cardViVayNo = findViewById(R.id.cardViVayNo);

        btnBack.setOnClickListener(v -> {
            finish();
        });


        cardViCoBan.setOnClickListener(v -> {
            Toast.makeText(this, "Chọn Ví Cơ Bản", Toast.LENGTH_SHORT).show();
            
        });

        cardTheTinDung.setOnClickListener(v -> {
            Toast.makeText(this, "Chọn Thẻ Tín Dụng", Toast.LENGTH_SHORT).show();
        });

        cardViVayNo.setOnClickListener(v -> {
            Toast.makeText(this, "Chọn Ví Vay Nợ", Toast.LENGTH_SHORT).show();
        });
    }
}