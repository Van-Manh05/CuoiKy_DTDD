
package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LichSuActivity extends AppCompatActivity {

    private TextView tvDateRange;
    private Calendar startDate;
    private Calendar endDate;
    private SimpleDateFormat displaySdf; // Format for displaying date
    private SimpleDateFormat parseSdf;   // Format for parsing date from GiaoDich object

    private List<GiaoDich> allGiaoDichList;
    private LichSuGiaoDichAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su);

        // --- Toolbar ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Date Handling Initialization ---
        tvDateRange = findViewById(R.id.tvDateRange);
        ImageView arrowLeft = findViewById(R.id.arrow_left_date);
        ImageView arrowRight = findViewById(R.id.arrow_right_date);
        displaySdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        parseSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Initialize dates to the current month
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        // --- RecyclerView and Adapter Setup ---
        allGiaoDichList = createDummyData();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LichSuGiaoDichAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initial display
        updateDateRangeTextView();
        filterAndDisplayTransactions();

        // --- Listeners ---
        arrowLeft.setOnClickListener(v -> {
            startDate.add(Calendar.MONTH, -1);
            endDate.setTime(startDate.getTime());
            endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            updateDateRangeTextView();
            filterAndDisplayTransactions();
        });

        arrowRight.setOnClickListener(v -> {
            startDate.add(Calendar.MONTH, 1);
            endDate.setTime(startDate.getTime());
            endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            updateDateRangeTextView();
            filterAndDisplayTransactions();
        });

        tvDateRange.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Chọn khoảng thời gian");
            builder.setSelection(new Pair<>(startDate.getTimeInMillis(), endDate.getTimeInMillis()));
            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
            picker.show(getSupportFragmentManager(), picker.toString());

            picker.addOnPositiveButtonClickListener(selection -> {
                long timeZoneOffset = TimeZone.getDefault().getOffset(selection.first);
                startDate.setTimeInMillis(selection.first + timeZoneOffset);
                endDate.setTimeInMillis(selection.second + timeZoneOffset);
                updateDateRangeTextView();
                filterAndDisplayTransactions();
            });
        });

        // --- Wallet Bottom Sheet ---
        Button btnChonVi = findViewById(R.id.btnChonVi);
        btnChonVi.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LichSuActivity.this);
            View bottomSheetView = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.bottom_sheet_chon_vi, findViewById(R.id.main), false);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });

        // --- Bottom Navigation and FAB ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_lich_su);
        FloatingActionButton fab = findViewById(R.id.fab_lich_su);

        // Set selected item
        bottomNav.setSelectedItemId(R.id.nav_lichsu);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tongquan) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_lichsu) {
                // Already on this screen
                return true;
            } else if (itemId == R.id.nav_taisan) {
                 startActivity(new Intent(getApplicationContext(), TaisanActivity.class));
                 overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_khampha) {
                 startActivity(new Intent(getApplicationContext(), KhamPhaActivity.class));
                 overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        fab.setOnClickListener(v -> {
            // Handle FAB click
            Toast.makeText(LichSuActivity.this, "Thêm giao dịch mới", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateDateRangeTextView() {
        String dateRangeString = displaySdf.format(startDate.getTime()) + " - " + displaySdf.format(endDate.getTime());
        tvDateRange.setText(dateRangeString);
    }

    private void filterAndDisplayTransactions() {
        List<GiaoDich> filteredList = new ArrayList<>();
        Calendar transactionCal = Calendar.getInstance();

        // Set end date to the very end of the day to include all transactions on that day
        Calendar inclusiveEndDate = (Calendar) endDate.clone();
        inclusiveEndDate.set(Calendar.HOUR_OF_DAY, 23);
        inclusiveEndDate.set(Calendar.MINUTE, 59);
        inclusiveEndDate.set(Calendar.SECOND, 59);

        for (GiaoDich gd : allGiaoDichList) {
            try {
                Date transactionDate = parseSdf.parse(gd.date);
                if (transactionDate != null) {
                    transactionCal.setTime(transactionDate);
                    // Check if the transaction date is within the selected range (inclusive)
                    if (!transactionCal.before(startDate) && !transactionCal.after(inclusiveEndDate)) {
                        filteredList.add(gd);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace(); // Handle parsing error
            }
        }
        adapter.updateList(filteredList);
    }

    private List<GiaoDich> createDummyData() {
        List<GiaoDich> list = new ArrayList<>();
        // Add some data for other months for testing
        list.add(new GiaoDich("10/10/2025", "Đi lại", "Tiền xăng", "-500.000", "Tiền mặt", R.drawable.store, false));
        list.add(new GiaoDich("25/12/2025", "Quà cáp", "Quà giáng sinh", "-1.500.000", "Tài khoản ngân hàng", R.drawable.bag_checked, false));

        list.add(new GiaoDich("18/11/2025", "Bán đồ", "Cc", "+5.000.000", "Tiền mặt", R.drawable.store, true));
        list.add(new GiaoDich("15/11/2025", "Nhà cửa - Hóa đơn", "Cc", "-6.000.000", "Tài khoản ngân hàng", R.drawable.lightning_bolt, false));
        list.add(new GiaoDich("15/11/2025", "Ăn uống - Cà phê", "Cc", "-1.000.000", "Tiền mặt", R.drawable.coffee, false));
        list.add(new GiaoDich("15/11/2025", "Cập nhật số dư", "", "+1.000.000", "Tài khoản ngân hàng", R.drawable.cloud_check_variant, true));
        list.add(new GiaoDich("15/11/2025", "Lương", "", "+20.000.000", "Tài khoản ngân hàng", R.drawable.bag_checked, true));
        return list;
    }

    // GiaoDich Model
    class GiaoDich {
        String date, categoryName, note, amount, wallet;
        int categoryIcon;
        boolean isIncome;
        boolean showDate;

        public GiaoDich(String date, String categoryName, String note, String amount, String wallet, int categoryIcon, boolean isIncome) {
            this.date = date;
            this.categoryName = categoryName;
            this.note = note;
            this.amount = amount;
            this.wallet = wallet;
            this.categoryIcon = categoryIcon;
            this.isIncome = isIncome;
        }
    }

    // LichSuGiaoDichAdapter
    class LichSuGiaoDichAdapter extends RecyclerView.Adapter<LichSuGiaoDichAdapter.ViewHolder> {

        private List<GiaoDich> giaoDichList;

        public LichSuGiaoDichAdapter(List<GiaoDich> giaoDichList) {
            this.giaoDichList = giaoDichList;
        }

        public void updateList(List<GiaoDich> newList) {
            this.giaoDichList.clear();
            if (newList != null && !newList.isEmpty()) {
                String lastDate = "";
                // Sort list by date before processing if needed, assuming it's pre-sorted for now
                for (GiaoDich gd : newList) {
                    if (!gd.date.equals(lastDate)) {
                        gd.showDate = true;
                        lastDate = gd.date;
                    } else {
                        gd.showDate = false;
                    }
                    this.giaoDichList.add(gd);
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lich_su_giao_dich, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GiaoDich giaoDich = giaoDichList.get(position);

            if (giaoDich.showDate) {
                holder.tvDate.setVisibility(View.VISIBLE);
                holder.tvDate.setText(giaoDich.date);
            } else {
                holder.tvDate.setVisibility(View.GONE);
            }

            holder.ivCategory.setImageResource(giaoDich.categoryIcon);
            holder.tvCategoryName.setText(giaoDich.categoryName);
            holder.tvNote.setText(giaoDich.note);
            holder.tvAmount.setText(giaoDich.amount);
            holder.tvWallet.setText(giaoDich.wallet);

            if (giaoDich.isIncome) {
                holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.positive_green));
            } else {
                holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.negative_red));
            }
        }

        @Override
        public int getItemCount() {
            return giaoDichList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvCategoryName, tvNote, tvAmount, tvWallet;
            ImageView ivCategory;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                tvNote = itemView.findViewById(R.id.tvNote);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvWallet = itemView.findViewById(R.id.tvWallet);
                ivCategory = itemView.findViewById(R.id.ivCategory);
            }
        }
    }
}
