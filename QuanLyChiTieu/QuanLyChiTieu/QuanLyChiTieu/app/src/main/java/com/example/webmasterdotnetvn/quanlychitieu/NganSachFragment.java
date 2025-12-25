package com.example.webmasterdotnetvn.quanlychitieu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button; // Import Button
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class NganSachFragment extends Fragment {

    private RecyclerView rcvBudget;
    private NganSachAdapter adapter;
    private List<NganSach> budgetList;

    // --- THAY ĐỔI Ở ĐÂY: Dùng Button thay vì ImageView ---
    private Button btnAddBudget;

    private TextView tvTotalSpent, tvTotalLimit;

    private FirebaseFirestore db;
    private String uid;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ngan_sach, container, false);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews(view);
        setupRecyclerView();
        loadBudgetData();

        btnAddBudget.setOnClickListener(v -> showAddBudgetDialog());

        return view;
    }

    private void initViews(View view) {
        rcvBudget = view.findViewById(R.id.rcvBudget);
        btnAddBudget = view.findViewById(R.id.btnAddBudget); // ID vẫn giữ nguyên
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);
        tvTotalLimit = view.findViewById(R.id.tvTotalLimit);
    }

    // ... (Các phần code bên dưới giữ nguyên không đổi) ...

    private void setupRecyclerView() {
        budgetList = new ArrayList<>();
        adapter = new NganSachAdapter(budgetList);
        rcvBudget.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvBudget.setAdapter(adapter);
    }

    private void loadBudgetData() {
        if (uid == null) return;
        db.collection("users").document(uid).collection("budgets")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    List<NganSach> tempBudgets = new ArrayList<>();
                    double totalLimit = 0;
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        String category = doc.getString("category");
                        Double limit = doc.getDouble("limitAmount");
                        if (limit == null) limit = 0.0;
                        tempBudgets.add(new NganSach(doc.getId(), category, limit, 0));
                        totalLimit += limit;
                    }
                    tvTotalLimit.setText("/ " + formatter.format(totalLimit) + " đ");
                    calculateSpending(tempBudgets);
                });
    }

    private void calculateSpending(List<NganSach> budgets) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);

        db.collection("users").document(uid).collection("transactions")
                .whereGreaterThanOrEqualTo("date", start.getTime())
                .whereLessThanOrEqualTo("date", end.getTime())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Double> spendingMap = new HashMap<>();
                    double totalSpentAll = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String type = doc.getString("type");
                        String cat = doc.getString("category");
                        Double amount = doc.getDouble("amount");
                        if ("CHI".equals(type) && amount != null && cat != null) {
                            spendingMap.put(cat, spendingMap.getOrDefault(cat, 0.0) + amount);
                            totalSpentAll += amount;
                        }
                    }
                    for (NganSach budget : budgets) {
                        if (spendingMap.containsKey(budget.getCategory())) {
                            budget.setSpentAmount(spendingMap.get(budget.getCategory()));
                        }
                    }
                    budgetList.clear();
                    budgetList.addAll(budgets);
                    adapter.notifyDataSetChanged();
                    tvTotalSpent.setText("Đã tiêu: " + formatter.format(totalSpentAll) + " đ");
                });
    }

    private void showAddBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm ngân sách mới");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final Spinner spinnerCategory = new Spinner(getContext());
        String[] categories = {"Ăn uống", "Di chuyển", "Mua sắm", "Giải trí", "Nhà cửa", "Hóa đơn", "Khác"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinAdapter);
        layout.addView(spinnerCategory);

        final EditText edtLimit = new EditText(getContext());
        edtLimit.setHint("Nhập hạn mức (Vd: 2000000)");
        edtLimit.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(edtLimit);

        builder.setView(layout);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String category = spinnerCategory.getSelectedItem().toString();
            String limitStr = edtLimit.getText().toString().trim();
            if (TextUtils.isEmpty(limitStr)) return;
            double limit = Double.parseDouble(limitStr);
            saveBudgetToFirebase(category, limit);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void saveBudgetToFirebase(String category, double limit) {
        Map<String, Object> budgetData = new HashMap<>();
        budgetData.put("category", category);
        budgetData.put("limitAmount", limit);
        db.collection("users").document(uid).collection("budgets")
                .add(budgetData)
                .addOnSuccessListener(doc -> Toast.makeText(getContext(), "Đã thêm ngân sách!", Toast.LENGTH_SHORT).show());
    }
}