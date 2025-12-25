// File: TongThuFragment.java
package com.example.webmasterdotnetvn.quanlychitieu;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class TongThuFragment extends Fragment {

    PieChart pieChart;
    RecyclerView recyclerView;
    ThuNhapAdapter adapter; // <-- Dùng Adapter mới
    List<ThuNhap> danhSachThuNhap; // <-- Dùng Model mới

    public TongThuFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tong_thu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View
        pieChart = view.findViewById(R.id.pieChart_tongThu);
        recyclerView = view.findViewById(R.id.recyclerView_tongThu);

        // 2. Cài đặt biểu đồ
        setupPieChart();

        // 3. Nạp dữ liệu biểu đồ (100% Lương)
        loadPieChartData();

        // 4. Nạp dữ liệu danh sách (Item Lương)
        loadRecyclerViewData();

        // 5. Tạo Adapter
        adapter = new ThuNhapAdapter(danhSachThuNhap);

        // 6. Cài đặt cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadRecyclerViewData() {
        danhSachThuNhap = new ArrayList<>();
        // (Dùng tạm icon ic_menu_home, bạn có thể tạo icon "ic_luong" và đổi)
        danhSachThuNhap.add(new ThuNhap(R.drawable.ic_salary, "Lương", 20000000));
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(58f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(40f, 40f, 40f, 40f);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        // Biểu đồ 100%
        entries.add(new PieEntry(100f, "Lương"));

        ArrayList<Integer> colors = new ArrayList<>();
        // Dùng màu xanh lá (giống màu chữ "Tổng thu")
        colors.add(Color.rgb(76, 175, 80));

        PieDataSet dataSet = new PieDataSet(entries, "Tổng Thu");
        dataSet.setColors(colors);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.6f);
        dataSet.setValueLinePart2Length(0.3f);
        dataSet.setValueLineColor(Color.GRAY);
        pieChart.setDrawEntryLabels(false);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400);
    }
}