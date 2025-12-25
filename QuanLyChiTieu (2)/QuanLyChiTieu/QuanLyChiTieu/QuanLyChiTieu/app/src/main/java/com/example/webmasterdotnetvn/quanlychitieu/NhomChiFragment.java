package com.example.webmasterdotnetvn.quanlychitieu; // Giữ package của bạn

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Import
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List; // Import

public class NhomChiFragment extends Fragment {

    // Khai báo View
    PieChart pieChart;
    RecyclerView recyclerView; // <-- KHAI BÁO MỚI
    ChiPhiAdapter adapter; // <-- KHAI BÁO MỚI
    List<ChiPhi> danhSachChiPhi; // <-- KHAI BÁO MỚI

    public NhomChiFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nhom_chi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ (tìm) PieChart
        pieChart = view.findViewById(R.id.pieChart_nhomChi);

        // 2. Cài đặt các thuộc tính cho biểu đồ
        setupPieChart();

        // 3. Nạp dữ liệu (data) vào biểu đồ
        loadPieChartData();

        // ===========================================
        // PHẦN LÀM VIỆC VỚI RECYCLERVIEW
        // ===========================================

        // 4. Ánh xạ RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_nhomChi);

        // 5. Nạp dữ liệu cho danh sách
        loadRecyclerViewData();

        // 6. Tạo Adapter
        adapter = new ChiPhiAdapter(danhSachChiPhi);

        // 7. Cài đặt cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadRecyclerViewData() {
        danhSachChiPhi = new ArrayList<>();
        // (Sau này bạn sẽ lấy dữ liệu này từ Database)
        // Dữ liệu giả lập theo hình của bạn
        danhSachChiPhi.add(new ChiPhi(R.drawable.ic_menu_home, "Nhà cửa", 6000000, 6000000));
        danhSachChiPhi.add(new ChiPhi(R.drawable.ic_meal, "Ăn uống", 3000000, 3000000));
        // (Mình dùng tạm icon ic_menu_home và ic_menu_crop, bạn có thể đổi)
    }

    /**
     * Hàm này dùng để "trang trí" biểu đồ
     */
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(58f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);

        // SỬA LỖI BỊ CHE SỐ
        pieChart.setExtraOffsets(40f, 40f, 40f, 40f);
        pieChart.setDrawEntryLabels(false);
    }

    /**
     * Hàm này dùng để nạp dữ liệu (giả lập)
     */
    private void loadPieChartData() {
        // Dữ liệu y hệt như cũ
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(66.67f, "Nhà cửa"));
        entries.add(new PieEntry(33.33f, "Ăn uống"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(236, 64, 122)); // Màu hồng
        colors.add(Color.rgb(41, 182, 246)); // Màu xanh

        PieDataSet dataSet = new PieDataSet(entries, "Nhóm Chi Tiêu");
        dataSet.setColors(colors);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.6f);
        dataSet.setValueLinePart2Length(0.3f);
        dataSet.setValueLineColor(Color.GRAY);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart) {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                // Trả về: "66.67% \n Nhà cửa"
                // (%.2f là làm tròn 2 số)
                return String.format("%.2f%%", value) + "\n" + pieEntry.getLabel();
            }
        });
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400);
    }
}