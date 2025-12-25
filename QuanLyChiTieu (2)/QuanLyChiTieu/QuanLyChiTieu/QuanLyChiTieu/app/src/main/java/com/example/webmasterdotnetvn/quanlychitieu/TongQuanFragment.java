package com.example.webmasterdotnetvn.quanlychitieu;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TongQuanFragment extends Fragment {

    // Views
    private PieChart pieChart;
    private TabLayout tabTime;
    private RecyclerView rcvGiaoDich;
    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense;

    // Data
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<GiaoDich> listGoc;
    private List<GiaoDich> listHienThi;
    private GiaoDichAdapter adapter;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    private int currentTabMode = 1; 

    // Danh sách các loại tính là Tiền ra (để vẽ biểu đồ chi)
    private final List<String> EXPENSE_TYPES = Arrays.asList("CHI", "ChoVay", "TraNo");
    // Danh sách các loại tính là Tền vào
    private final List<String> INCOME_TYPES = Arrays.asList("THU", "DiVay", "ThuNo");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tong_quan, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listGoc = new ArrayList<>();
        listHienThi = new ArrayList<>();

        pieChart = view.findViewById(R.id.pieChart);
        tabTime = view.findViewById(R.id.tabTime);
        rcvGiaoDich = view.findViewById(R.id.recyclerViewTransactions);
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);

        setupChartConfig();
        setupRecyclerView();
        setupTabs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromFirebase();
    }

    private void setupRecyclerView() {
        rcvGiaoDich.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GiaoDichAdapter(getContext(), listHienThi);
        rcvGiaoDich.setAdapter(adapter);
    }

    private void setupTabs() {
        tabTime.addTab(tabTime.newTab().setText("Theo Tuần"));
        tabTime.addTab(tabTime.newTab().setText("Theo Tháng"));
        tabTime.addTab(tabTime.newTab().setText("Theo Năm"));

        TabLayout.Tab monthTab = tabTime.getTabAt(1);
        if (monthTab != null) monthTab.select();

        tabTime.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabMode = tab.getPosition();
                filterDataByTime(currentTabMode);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadDataFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .collection("transactions")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        listGoc.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            GiaoDich gd = doc.toObject(GiaoDich.class);
                            if (gd != null) {
                                gd.setId(doc.getId());
                                listGoc.add(gd);
                            }
                        }
                        filterDataByTime(currentTabMode);
                    }
                });
    }

    private void filterDataByTime(int mode) {
        listHienThi.clear();

        Calendar cal = Calendar.getInstance();
        Date startDate, endDate;

        if (mode == 0) { // TUẦN
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            setStartOfDay(cal);
            startDate = cal.getTime();
            cal.add(Calendar.DATE, 6);
            setEndOfDay(cal);
            endDate = cal.getTime();
        } else if (mode == 1) { // THÁNG
            cal.set(Calendar.DAY_OF_MONTH, 1);
            setStartOfDay(cal);
            startDate = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            setEndOfDay(cal);
            endDate = cal.getTime();
        } else { // NĂM
            cal.set(Calendar.DAY_OF_YEAR, 1);
            setStartOfDay(cal);
            startDate = cal.getTime();
            cal.set(Calendar.MONTH, 11);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            setEndOfDay(cal);
            endDate = cal.getTime();
        }

        double tongThu = 0;
        double tongChi = 0;

        for (GiaoDich gd : listGoc) {
            // 1. Lọc theo thời gian
            if (gd.getDate() != null && gd.getDate().compareTo(startDate) >= 0 && gd.getDate().compareTo(endDate) <= 0) {
                listHienThi.add(gd);

                // 2. Tính toán tổng tiền theo Loại giao dịch mới
                String type = gd.getType(); // CHI, THU, DiVay, ChoVay...

                if (INCOME_TYPES.contains(type)) {
                    // Tiền vào (Thu nhập, Đi vay, Thu nợ)
                    tongThu += gd.getAmount();
                } else if (EXPENSE_TYPES.contains(type)) {
                    // Tiền ra (Chi tiêu, Cho vay, Trả nợ)
                    tongChi += gd.getAmount();
                }
                // Loại "CHUYEN" (Chuyển khoản) không tính vào tổng Thu/Chi vì là trung gian
            }
        }

        adapter.notifyDataSetChanged();
        updateUI(tongThu, tongChi);
        updateChart(listHienThi);
    }

    private void updateUI(double thu, double chi) {
        double soDu = thu - chi;
        tvTotalIncome.setText(formatter.format(thu) + " đ");
        tvTotalExpense.setText(formatter.format(chi) + " đ");
        tvTotalBalance.setText(formatter.format(soDu) + " đ");
    }

    private void updateChart(List<GiaoDich> transactions) {
        Map<String, Float> categoryMap = new HashMap<>();
        boolean hasData = false;

        for (GiaoDich gd : transactions) {
            // Chỉ vẽ biểu đồ cho các khoản TIỀN RA (Expense)
            // Bao gồm: Chi tiêu, Cho vay, Trả nợ
            if (EXPENSE_TYPES.contains(gd.getType())) {
                String catName = gd.getCategory();
                // Nếu là vay nợ, Category chính là tên người, vẫn hiện lên biểu đồ
                float current = categoryMap.containsKey(catName) ? categoryMap.get(catName) : 0;
                categoryMap.put(catName, current + (float) gd.getAmount());
                hasData = true;
            }
        }

        if (!hasData) {
            pieChart.clear();
            pieChart.setNoDataText("Chưa có chi tiêu nào");
            pieChart.invalidate();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        for (String key : categoryMap.keySet()) {
            entries.add(new PieEntry(categoryMap.get(key), key));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Cấu trúc chi");
        pieChart.animateY(800);
        pieChart.invalidate();
    }

    private void setStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private void setEndOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
    }

    private void setupChartConfig() {
        pieChart.setDescription(null);
        pieChart.setNoDataText("Đang tải...");
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false);
    }
}