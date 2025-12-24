package com.example.webmasterdotnetvn.quanlychitieu;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ThemGiaoDichActivity extends AppCompatActivity {

    private EditText edtAmount, edtGhiChu;
    private ImageView btnClose;
    private TabLayout tabLayoutType;
    private MaterialButton btnSave;
    private SwitchMaterial switchBaoCao;

    private View scrollViewDebtOptions, layoutNgayTra;
    private TextView tvLabelRow1, tvValueRow1;
    private TextView tvLabelRow2, tvValueRow2;
    private TextView tvDate;
    private ImageView imgRow1, imgRow2;

    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_giao_dich);

        selectedCalendar = Calendar.getInstance();

        initViews();
        setupTabs();
        setupEvents();
        updateDateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tự động hiện bàn phím khi màn hình vừa mở lên
        if (edtAmount != null) {
            // Delay 300ms để đảm bảo giao diện đã vẽ xong hoàn toàn
            edtAmount.postDelayed(this::showKeyboard, 300);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Ẩn bàn phím khi thoát app hoặc chuyển màn hình để tránh lỗi
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtAmount.getWindowToken(), 0);
        }
    }

    private void initViews() {
        edtAmount = findViewById(R.id.edtAmount);
        btnClose = findViewById(R.id.btnClose);
        tabLayoutType = findViewById(R.id.tabLayoutType);
        btnSave = findViewById(R.id.btnSave);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        switchBaoCao = findViewById(R.id.switchBaoCao);

        scrollViewDebtOptions = findViewById(R.id.scrollViewDebtOptions);
        layoutNgayTra = findViewById(R.id.layoutNgayTra);

        tvLabelRow1 = findViewById(R.id.tvLabelRow1);
        tvValueRow1 = findViewById(R.id.tvValueRow1);
        imgRow1 = findViewById(R.id.imgRow1);

        tvLabelRow2 = findViewById(R.id.tvLabelRow2);
        tvValueRow2 = findViewById(R.id.tvValueRow2);
        imgRow2 = findViewById(R.id.imgRow2);

        tvDate = findViewById(R.id.tvDate);
    }

    // --- HÀM QUAN TRỌNG: HIỆN BÀN PHÍM ---
    private void showKeyboard() {
        edtAmount.requestFocus();
        // Đặt con trỏ về cuối
        edtAmount.setSelection(edtAmount.getText().length());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            // Dùng SHOW_FORCED để ép buộc bàn phím hiện lên (kể cả khi máy ảo cố tình ẩn)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void showDatePickerDialog() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                    updateDateDisplay();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        Calendar today = Calendar.getInstance();
        if (selectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                selectedCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            tvDate.setText("Hôm nay");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvDate.setText(sdf.format(selectedCalendar.getTime()));
        }
    }

    private void setupTabs() {
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Chi tiêu"));
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Thu nhập"));
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Vay nợ"));
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Chuyển khoản"));

        // Load UI mặc định cho tab đầu tiên
        updateUIForTab(0);

        tabLayoutType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateUIForTab(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateUIForTab(int position) {
        if (scrollViewDebtOptions != null) scrollViewDebtOptions.setVisibility(View.GONE);
        if (layoutNgayTra != null) layoutNgayTra.setVisibility(View.GONE);

        switch (position) {
            case 0: // CHI TIÊU
                tvLabelRow1.setText("Mục chi"); tvValueRow1.setText("Chọn mục chi");
                imgRow1.setImageResource(R.drawable.ic_category);
                tvLabelRow2.setText("Ví"); tvValueRow2.setText("Chọn ví");
                imgRow2.setImageResource(R.drawable.ic_wallet);
                break;
            case 1: // THU NHẬP
                tvLabelRow1.setText("Mục thu"); tvValueRow1.setText("Chọn mục thu");
                imgRow1.setImageResource(R.drawable.ic_category);
                tvLabelRow2.setText("Ví"); tvValueRow2.setText("Chọn ví");
                imgRow2.setImageResource(R.drawable.ic_wallet);
                break;
            case 2: // VAY NỢ
                if (scrollViewDebtOptions != null) scrollViewDebtOptions.setVisibility(View.VISIBLE);
                if (layoutNgayTra != null) layoutNgayTra.setVisibility(View.VISIBLE);
                tvLabelRow1.setText("Đối tượng"); tvValueRow1.setText("Chọn người");
                imgRow1.setImageResource(R.drawable.ic_person);
                tvLabelRow2.setText("Ví"); tvValueRow2.setText("Chọn ví");
                imgRow2.setImageResource(R.drawable.ic_wallet);
                break;
            case 3: // CHUYỂN KHOẢN
                tvLabelRow1.setText("Từ"); tvValueRow1.setText("Chọn ví nguồn");
                imgRow1.setImageResource(R.drawable.ic_wallet);
                tvLabelRow2.setText("Đến"); tvValueRow2.setText("Chọn ví đích");
                imgRow2.setImageResource(R.drawable.ic_wallet);
                break;
        }
    }

    private void setupEvents() {
        btnClose.setOnClickListener(v -> finish());

        // SỰ KIỆN: Nhấn vào ô nhập tiền (số 0) -> Hiện bàn phím ngay
        edtAmount.setOnClickListener(v -> showKeyboard());

        // SỰ KIỆN: Nhấn vào toàn bộ vùng chứa số tiền -> Hiện bàn phím (đề phòng bấm lệch)
        // Bạn có thể gán thêm ID cho layout chứa edtAmount trong XML nếu muốn vùng bấm rộng hơn

        btnSave.setOnClickListener(v -> {
            String amount = edtAmount.getText().toString();
            if (amount.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đã lưu: " + amount, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    btnSave.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    btnSave.setTextColor(Color.WHITE);
                } else {
                    btnSave.setBackgroundColor(Color.parseColor("#009688"));
                    btnSave.setTextColor(Color.WHITE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener notImpl = v -> Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        findViewById(R.id.btnRow1).setOnClickListener(notImpl);
        findViewById(R.id.btnRow2).setOnClickListener(notImpl);
        findViewById(R.id.btnRowDate).setOnClickListener(v -> showDatePickerDialog());
    }
}