// File: MucChiFragment.java
package com.example.webmasterdotnetvn.quanlychitieu; // Giữ package của bạn

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

public class MucChiFragment extends Fragment {

    // Khai báo View
    PieChart pieChart;
    RecyclerView recyclerView;
    ChiPhiAdapter adapter; // DÙNG LẠI ADAPTER CŨ
    List<ChiPhi> danhSachChiPhi; // DÙNG LẠI MODEL CŨ

    public MucChiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_muc_chi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ (tìm) PieChart và RecyclerView
        pieChart = view.findViewById(R.id.pieChart_mucChi);
        recyclerView = view.findViewById(R.id.recyclerView_mucChi);

        // 2. Cài đặt các thuộc tính cho biểu đồ
        setupPieChart();

        // 3. Nạp dữ liệu (data) vào biểu đồ
        loadPieChartData();

        // 4. Nạp dữ liệu cho danh sách
        loadRecyclerViewData();

        // 5. Tạo Adapter (DÙNG LẠI)
        adapter = new ChiPhiAdapter(danhSachChiPhi);

        // 6. Cài đặt cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Hàm này dùng để nạp dữ liệu cho DANH SÁCH (RecyclerView)
     * DỮ LIỆU MỚI THEO YÊU CẦU CỦA BẠN
     */
    private void loadRecyclerViewData() {
        danhSachChiPhi = new ArrayList<>();
        // Dữ liệu giả lập cho "Mục chi"
        danhSachChiPhi.add(new ChiPhi(R.drawable.ic_electricity_bill, "Hoá đơn điện", 6000000, 6000000));
        danhSachChiPhi.add(new ChiPhi(R.drawable.ic_eat_restaurant, "Ăn nhà hàng", 2000000, 2000000));
        danhSachChiPhi.add(new ChiPhi(R.drawable.ic_coffee, "Cà phê", 1000000, 1000000));
        danhSachChiPhi.add(new ChiPhi(R.drawable.ic_coffee, "Cà phê", 1000000, 1000000));
        // (Mình dùng tạm mấy icon có sẵn, bạn có thể đổi)

    }

    /**
     * Hàm này dùng để "trang trí" biểu đồ
     */
    private void setupPieChart() {
        // (Code y hệt NhomChiFragment)
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(58f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(40f, 40f, 40f, 40f); // Chừa lề
        pieChart.setDrawEntryLabels(false);
    }

    /**
     * Hàm này dùng để nạp dữ liệu cho BIỂU ĐỒ TRÒN
     * DỮ LIỆU MỚI THEO YÊU CẦU CỦA BẠN
     */
    private void loadPieChartData() {
        // Dữ liệu giả lập cho "Mục chi"
        // Tổng là 9.000.000
        // Hoá đơn điện: 6tr (66.67%)
        // Ăn nhà hàng: 2tr (22.22%)
        // Cà phê: 1tr (11.11%)
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(66.67f, "Hoá đơn điện"));
        entries.add(new PieEntry(22.22f, "Ăn nhà hàng"));
        entries.add(new PieEntry(11.11f, "Cà phê"));

        // Dùng 3 màu
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(236, 64, 122)); // Màu hồng
        colors.add(Color.rgb(41, 182, 246)); // Màu xanh 1
        colors.add(Color.rgb(129, 212, 250)); // Màu xanh 2 (nhạt hơn)

        PieDataSet dataSet = new PieDataSet(entries, "Mục Chi Tiêu");
        dataSet.setColors(colors);

        // (Code trang trí y hệt NhomChiFragment)
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