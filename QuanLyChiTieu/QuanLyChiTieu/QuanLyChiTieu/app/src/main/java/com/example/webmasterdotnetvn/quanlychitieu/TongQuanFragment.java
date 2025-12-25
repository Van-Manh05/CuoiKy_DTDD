package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TongQuanFragment extends Fragment {

    // Views
    private TextView tvUserNameHome, tvTotalBalance, tvIncomeCard, tvExpenseCard;
    private TextView tvBudgetPercent, tvBudgetStatus;
    private ProgressBar progressBarBudget;
    private PieChart pieChart;

    // Quick Buttons
    private LinearLayout btnQuickNap, btnQuickRut, btnQuickVi, btnQuickHistory;

    // Data
    private FirebaseFirestore db;
    private String uid;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tong_quan, container, false); // Đảm bảo tên layout đúng

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews(view);
        setupQuickActions();
        loadUserData();
        loadFinancialData(); // Tải số dư, thu chi

        return view;
    }

    private void initViews(View view) {
        tvUserNameHome = view.findViewById(R.id.tvUserNameHome);
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvIncomeCard = view.findViewById(R.id.tvIncomeCard);
        tvExpenseCard = view.findViewById(R.id.tvExpenseCard);

        tvBudgetPercent = view.findViewById(R.id.tvBudgetPercent);
        tvBudgetStatus = view.findViewById(R.id.tvBudgetStatus);
        progressBarBudget = view.findViewById(R.id.progressBarBudget);

        pieChart = view.findViewById(R.id.pieChart);

        btnQuickNap = view.findViewById(R.id.btnQuickNapTien);
        btnQuickRut = view.findViewById(R.id.btnQuickRutTien);
        btnQuickVi = view.findViewById(R.id.btnQuickVi);
        btnQuickHistory = view.findViewById(R.id.btnQuickHistory);
    }

    private void setupQuickActions() {
        btnQuickNap.setOnClickListener(v -> startActivity(new Intent(getContext(), NapTienActivity.class)));
        btnQuickRut.setOnClickListener(v -> startActivity(new Intent(getContext(), RutTienActivity.class)));
        btnQuickVi.setOnClickListener(v -> startActivity(new Intent(getContext(), QuanLyViActivity.class)));
        btnQuickHistory.setOnClickListener(v -> startActivity(new Intent(getContext(), LichSuActivity.class)));
    }

    private void loadUserData() {
        if (uid == null) return;
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String name = doc.getString("fullName");
                if (name == null) name = doc.getString("name");
                tvUserNameHome.setText(name != null ? name : "Người dùng");
            }
        });
    }

    private void loadFinancialData() {
        if (uid == null) return;

        // 1. TÍNH TỔNG TÀI SẢN (Từ Collection Wallets)
        db.collection("users").document(uid).collection("wallets")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    double totalAssets = 0;
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Double bal = doc.getDouble("balance");
                        if (bal != null) totalAssets += bal;
                    }
                    tvTotalBalance.setText(formatter.format(totalAssets) + " đ");
                });

        // 2. TÍNH THU/CHI THÁNG NÀY (Từ Collection Transactions)
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1); // Đầu tháng
        start.set(Calendar.HOUR_OF_DAY, 0);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH)); // Cuối tháng
        end.set(Calendar.HOUR_OF_DAY, 23);

        db.collection("users").document(uid).collection("transactions")
                .whereGreaterThanOrEqualTo("date", start.getTime())
                .whereLessThanOrEqualTo("date", end.getTime())
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    double totalIncome = 0;
                    double totalExpense = 0;
                    Map<String, Double> categoryMap = new HashMap<>(); // Dữ liệu cho biểu đồ

                    for (QueryDocumentSnapshot doc : value) {
                        Double amount = doc.getDouble("amount");
                        String type = doc.getString("type"); // "THU" hoặc "CHI"
                        String cat = doc.getString("category");

                        if (amount == null) amount = 0.0;

                        // Logic phân loại Thu/Chi
                        boolean isExpense = "CHI".equals(type) || (cat != null && !cat.equals("Nạp tiền"));

                        if (isExpense) {
                            totalExpense += amount;
                            // Gom nhóm cho biểu đồ
                            if (cat != null) {
                                categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + amount);
                            }
                        } else {
                            totalIncome += amount;
                        }
                    }

                    // Cập nhật UI Thẻ ATM
                    tvIncomeCard.setText("+ " + formatter.format(totalIncome));
                    tvExpenseCard.setText("- " + formatter.format(totalExpense));

                    // Cập nhật Thanh Ngân Sách
                    updateBudgetBar(totalIncome, totalExpense);

                    // Cập nhật Biểu đồ
                    updatePieChart(categoryMap);
                });
    }

    private void updateBudgetBar(double income, double expense) {
        // Giả sử Ngân sách = 80% Thu nhập (hoặc tối thiểu 5 triệu)
        double budgetLimit = income > 0 ? income * 0.8 : 5000000;

        int percent = (int) ((expense / budgetLimit) * 100);
        progressBarBudget.setProgress(percent);
        tvBudgetPercent.setText(percent + "%");

        if (percent > 100) {
            tvBudgetStatus.setText("Cảnh báo: Vượt quá ngân sách!");
            tvBudgetStatus.setTextColor(Color.RED);
            progressBarBudget.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (percent > 80) {
            tvBudgetStatus.setText("Sắp hết hạn mức");
            tvBudgetStatus.setTextColor(Color.parseColor("#FF9800")); // Cam
            progressBarBudget.getProgressDrawable().setColorFilter(Color.parseColor("#FF9800"), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            tvBudgetStatus.setText("Chi tiêu an toàn");
            tvBudgetStatus.setTextColor(Color.parseColor("#4CAF50")); // Xanh
            progressBarBudget.getProgressDrawable().setColorFilter(Color.parseColor("#4CAF50"), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void updatePieChart(Map<String, Double> data) {
        List<PieEntry> entries = new ArrayList<>();
        for (String key : data.keySet()) {
            entries.add(new PieEntry(data.get(key).floatValue(), key));
        }

        if (entries.isEmpty()) {
            pieChart.setCenterText("Chưa có\nchi tiêu");
            pieChart.setData(null);
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Chi tiêu\nTháng này");
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}