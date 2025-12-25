// File: ChenhLechFragment.java
package com.example.webmasterdotnetvn.quanlychitieu; // Giữ package của bạn

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// === Import các thư viện BarChart ===
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ChenhLechFragment extends Fragment {

    BarChart barChart;

    public ChenhLechFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chenh_lech, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        barChart = view.findViewById(R.id.barChart_chenhLech);

        // 2. Trang trí
        setupBarChart();

        // 3. Nạp dữ liệu
        loadBarChartData();
    }

    private void setupBarChart() {
        // Tắt các tương tác (zoom, kéo)
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setTouchEnabled(false);

        // Tắt mô tả (description)
        barChart.getDescription().setEnabled(false);

        // Cấu hình trục X (Trục dưới: "Thg 11")
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Chữ nằm ở dưới
        xAxis.setDrawGridLines(false); // Tắt lưới dọc
        xAxis.setTextSize(12f);
        // Đặt tên cho cột (0 -> "Thg 11")
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Thg 11"}));

        // Cấu hình trục Y (Trái)
        barChart.getAxisLeft().setAxisMinimum(0f); // Bắt đầu từ 0
        barChart.getAxisLeft().setTextSize(12f);

        // Tắt trục Y (Phải)
        barChart.getAxisRight().setEnabled(false);

        // Cấu hình Chú thích (Legend: ô màu "Thu", "Chi")
        Legend legend = barChart.getLegend();
        legend.setTextSize(14f);
        legend.setForm(Legend.LegendForm.SQUARE); // Hình vuông
        legend.setFormSize(14f);
        legend.setXEntrySpace(20f); // Khoảng cách giữa 2 mục
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Căn giữa
    }

    private void loadBarChartData() {
        // === DỮ LIỆU CỘT CHỒNG ===
        // Dữ liệu giả lập từ app của bạn:
        // Chi = 9tr(màu đỏ)
        // Thu = 20tr(màu xanh)

        ArrayList<BarEntry> entries = new ArrayList<>();

        // 1. Tạo một "cột" (Entry)
        // Cột này nằm ở vị trí X = 0
        // Nó chứa 2 giá trị "chồng" lên nhau: Chi trước, Thu sau
        entries.add(new BarEntry(
                0f, // Vị trí (cột đầu tiên)
                new float[]{9000000f, 20000000f} // Dữ liệu: [Chi, Thu]
        ));

        // 2. Tạo DataSet (bộ dữ liệu)
        BarDataSet dataSet = new BarDataSet(entries, ""); // Tiêu đề rỗng

        // 3. Đặt màu cho các phần "chồng"
        // (Phải khớp với thứ tự ở trên: Đỏ cho Chi, Xanh cho Thu)
        dataSet.setColors(
                Color.rgb(244, 67, 54),  // Màu đỏ (Chi)
                Color.rgb(102, 187, 106) // Màu xanh lá (Thu)
        );

        // 4. Đặt tên cho các phần "chồng" (để hiện ở Legend)
        dataSet.setStackLabels(new String[]{"Chi", "Thu"});

        // 5. Tắt việc vẽ số % trên cột (cho đỡ rối)
        dataSet.setDrawValues(false);

        // 6. Nạp data vào biểu đồ
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate(); // Vẽ lại
        barChart.animateY(1500); // Thêm hiệu ứng
    }
}