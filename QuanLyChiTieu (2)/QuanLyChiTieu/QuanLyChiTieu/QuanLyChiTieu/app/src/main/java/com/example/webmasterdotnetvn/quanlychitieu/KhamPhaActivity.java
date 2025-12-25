package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhamPhaActivity extends AppCompatActivity {

    // Views
    private TextView tvUserName, tvUserEmail, tvTotalExpense;
    private LinearLayout btnCategorySetting, btnWalletSetting, btnChangePassword, btnLogout;
    private BottomNavigationView bottomNavigationView;
    private PieChart pieChart;

    // Firebase & Data
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kham_pha);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        loadUserInfo();
        setupPieChart(); // Cấu hình biểu đồ
        loadChartData(); // Tải dữ liệu biểu đồ
        setupEvents();
        setupBottomNav();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnCategorySetting = findViewById(R.id.btnCategorySetting);
        btnWalletSetting = findViewById(R.id.btnWalletSetting);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Chart views
        pieChart = findViewById(R.id.pieChart);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvUserEmail.setText(currentUser.getEmail());
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String fullName = doc.getString("fullName");
                                if (fullName == null) fullName = doc.getString("name");
                                tvUserName.setText(fullName != null ? fullName : "Người dùng");
                            }
                        }
                    });
        }
    }

    // --- XỬ LÝ BIỂU ĐỒ ---
    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterText("Chi tiêu\nTháng này");
        pieChart.setCenterTextSize(14f);
        pieChart.animateY(1000);

        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setWordWrapEnabled(true);
    }

    private void loadChartData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid()).collection("transactions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Double> categoryMap = new HashMap<>();
                    double totalExpense = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String type = doc.getString("type");
                        String category = doc.getString("category");
                        Double amount = doc.getDouble("amount");

                        boolean isExpense = "CHI".equals(type) || (category != null && !category.equals("Nạp tiền"));

                        if (isExpense && amount != null && category != null) {
                            totalExpense += amount;
                            if (categoryMap.containsKey(category)) {
                                categoryMap.put(category, categoryMap.get(category) + amount);
                            } else {
                                categoryMap.put(category, amount);
                            }
                        }
                    }

                    showDataOnChart(categoryMap, totalExpense);
                });
    }

    private void showDataOnChart(Map<String, Double> categoryMap, double totalExpense) {
        List<PieEntry> entries = new ArrayList<>();
        for (String key : categoryMap.keySet()) {
            float value = categoryMap.get(key).floatValue();
            entries.add(new PieEntry(value, key));
        }

        if (entries.isEmpty()) {
            pieChart.setCenterText("Chưa có\ndữ liệu");
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

        tvTotalExpense.setText("Tổng chi: " + formatter.format(totalExpense) + " đ");
    }

    // --- CÀI ĐẶT ---
    private void setupEvents() {
        btnCategorySetting.setOnClickListener(v -> startActivity(new Intent(this, CategorySettingsActivity.class)));
        btnWalletSetting.setOnClickListener(v -> startActivity(new Intent(this, QuanLyViActivity.class)));
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText edtOldPass = new EditText(this);
        edtOldPass.setHint("Mật khẩu hiện tại");
        edtOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(edtOldPass);

        final EditText edtNewPass = new EditText(this);
        edtNewPass.setHint("Mật khẩu mới (tối thiểu 6 ký tự)");
        edtNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(edtNewPass);

        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String oldPass = edtOldPass.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            changePasswordFirebase(oldPass, newPass);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void changePasswordFirebase(String oldPass, String newPass) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !TextUtils.isEmpty(oldPass) && !TextUtils.isEmpty(newPass)) {
            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Lỗi: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // --- ĐIỀU HƯỚNG ---
    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_khampha);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_taisan) {
                startActivity(new Intent(this, TaisanActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_ngansach) {
                // --- CẬP NHẬT: Gửi tín hiệu mở tab Ngân sách ---
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("open_budget", true);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_lichsu) {
                startActivity(new Intent(this, LichSuActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_khampha) {
                return true;
            }
            return false;
        });
    }
}