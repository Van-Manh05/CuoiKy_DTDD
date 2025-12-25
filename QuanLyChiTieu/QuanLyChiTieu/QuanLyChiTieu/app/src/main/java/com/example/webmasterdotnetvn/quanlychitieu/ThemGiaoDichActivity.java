package com.example.webmasterdotnetvn.quanlychitieu;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ThemGiaoDichActivity extends AppCompatActivity {

    // --- 1. KHAI BÁO VIEW ---
    private ImageView btnClose;
    private EditText edtAmount, edtGhiChu;
    private MaterialButton btnSave;

    // Các thành phần điều hướng
    private TabLayout tabLayoutType;
    private HorizontalScrollView scrollViewDebtOptions;
    private ChipGroup chipGroupDebt;
    private SwitchMaterial switchBaoCao;

    // Các dòng chọn dữ liệu (Row)
    private LinearLayout btnRow1, btnRow2, btnRowDate;
    private TextView tvLabelRow1, tvValueRow1; // Dòng 1 (Mục chi / Người vay / Từ ví)
    private TextView tvLabelRow2, tvValueRow2; // Dòng 2 (Ví / Đến ví)
    private TextView tvDate;

    // --- 2. BIẾN DỮ LIỆU ---
    private FirebaseFirestore db;
    private String uid;

    private Calendar calendar;
    private Date selectedDate;
    private String selectedCategory = "";
    private String selectedWallet = "Tiền mặt"; // Mặc định

    // Logic Loại giao dịch
    // Mạnh  0: Chi tiêu, 1: Thu nhập, 2: Vay nợ, 3: Chuyển khoản
    private int currentTabPosition = 0;
    private String debtType = "DiVay"; // Mặc định của tab Vay nợ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_giao_dich);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ và Khởi tạo
        initViews();
        setupTabs(); // Cài đặt các tab (Chi, Thu, Vay, Chuyển)
        setupDefaults();
        setupEvents();
    }

    private void initViews() {
        btnClose = findViewById(R.id.btnClose);
        edtAmount = findViewById(R.id.edtAmount);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        btnSave = findViewById(R.id.btnSave);

        tabLayoutType = findViewById(R.id.tabLayoutType);
        scrollViewDebtOptions = findViewById(R.id.scrollViewDebtOptions);
        chipGroupDebt = findViewById(R.id.chipGroupDebt);
        switchBaoCao = findViewById(R.id.switchBaoCao);

        // Row 1
        btnRow1 = findViewById(R.id.btnRow1);
        tvLabelRow1 = findViewById(R.id.tvLabelRow1);
        tvValueRow1 = findViewById(R.id.tvValueRow1);

        // Row 2
        btnRow2 = findViewById(R.id.btnRow2);
        tvLabelRow2 = findViewById(R.id.tvLabelRow2);
        tvValueRow2 = findViewById(R.id.tvValueRow2);

        // Row Date
        btnRowDate = findViewById(R.id.btnRowDate);
        tvDate = findViewById(R.id.tvDate);
    }

    private void setupDefaults() {
        calendar = Calendar.getInstance();
        selectedDate = calendar.getTime();
        updateDateDisplay();

        // Mặc định focus vào nhập tiền
        edtAmount.requestFocus();
    }

    // --- 3. CẤU HÌNH TAB & CHIP (LOGIC UI PHỨC TẠP) ---
    private void setupTabs() {
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Chi tiêu"));
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Thu nhập"));
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Vay nợ"));
        tabLayoutType.addTab(tabLayoutType.newTab().setText("Chuyển khoản"));

        tabLayoutType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                updateUIByTab();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Xử lý khi chọn Chip (Đi vay, Trả nợ...)
        chipGroupDebt.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipVay) {
                debtType = "DiVay";
                tvLabelRow1.setText("Người cho vay");
                tvValueRow1.setText("Chọn người");
            } else if (checkedId == R.id.chipTraNo) {
                debtType = "TraNo";
                tvLabelRow1.setText("Người nhận tiền");
                tvValueRow1.setText("Chọn người");
            } else if (checkedId == R.id.chipChoVay) {
                debtType = "ChoVay";
                tvLabelRow1.setText("Người vay");
                tvValueRow1.setText("Chọn người");
            } else if (checkedId == R.id.chipThuNo) {
                debtType = "ThuNo";
                tvLabelRow1.setText("Người trả tiền");
                tvValueRow1.setText("Chọn người");
            }
        });
    }

    // Hàm cập nhật giao diện dựa trên Tab đang chọn
    private void updateUIByTab() {
        // Reset giá trị hiển thị dòng 1
        tvValueRow1.setText("Chọn danh mục");
        selectedCategory = "";

        switch (currentTabPosition) {
            case 0: // CHI TIÊU
                scrollViewDebtOptions.setVisibility(View.GONE);
                tvLabelRow1.setText("Mục chi");
                tvLabelRow2.setText("Ví");
                edtAmount.setTextColor(Color.parseColor("#F44336")); // Đỏ
                break;

            case 1: // THU NHẬP
                scrollViewDebtOptions.setVisibility(View.GONE);
                tvLabelRow1.setText("Mục thu");
                tvLabelRow2.setText("Ví");
                edtAmount.setTextColor(Color.parseColor("#4CAF50")); // Xanh
                break;

            case 2: // VAY NỢ
                scrollViewDebtOptions.setVisibility(View.VISIBLE);
                chipGroupDebt.check(R.id.chipVay); // Mặc định chọn chip đầu
                tvLabelRow1.setText("Người cho vay");
                tvLabelRow2.setText("Ví");
                tvValueRow1.setText("Chọn người");
                edtAmount.setTextColor(Color.parseColor("#2196F3")); // Xanh dương
                break;

            case 3: // CHUYỂN KHOẢN
                scrollViewDebtOptions.setVisibility(View.GONE);
                tvLabelRow1.setText("Từ ví");
                tvValueRow1.setText("Chọn ví nguồn");
                tvLabelRow2.setText("Đến ví");
                tvValueRow2.setText("Chọn ví đích");
                edtAmount.setTextColor(Color.BLACK);
                break;
        }
    }

    // --- 4. XỬ LÝ SỰ KIỆN CLICK ---
    private void setupEvents() {
        btnClose.setOnClickListener(v -> finish());

        // Chọn Ngày
        btnRowDate.setOnClickListener(v -> showDatePicker());

        // Chọn Dòng 1 (Mục chi / Người / Ví nguồn)
        btnRow1.setOnClickListener(v -> showCategoryOrPersonDialog());

        // Chọn Dòng 2 (Ví / Ví đích)
        btnRow2.setOnClickListener(v -> showWalletDialog(currentTabPosition == 3)); // True nếu là ví đích

        // Nút Lưu
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    // --- 5. CÁC DIALOG CHỌN DỮ LIỆU ---

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
            updateDateDisplay();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(selectedDate));
    }

    private void showCategoryOrPersonDialog() {
        String title = tvLabelRow1.getText().toString();
        String[] items;

        if (currentTabPosition == 0) { // Chi tiêu
            items = new String[]{"Ăn uống", "Đi lại", "Nhà cửa", "Hóa đơn", "Mua sắm", "Giải trí"};
        } else if (currentTabPosition == 1) { // Thu nhập
            items = new String[]{"Lương", "Thưởng", "Bán đồ", "Đầu tư"};
        } else if (currentTabPosition == 2) { // Vay nợ (Chọn người)
            items = new String[]{"Nguyễn Văn A", "Trần Thị B", "Bạn bè", "Người thân"};
        } else { // Chuyển khoản (Chọn Ví nguồn)
            items = new String[]{"Tiền mặt", "Vietcombank", "Momo", "Heo đất"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn " + title);
        builder.setItems(items, (dialog, which) -> {
            selectedCategory = items[which];
            tvValueRow1.setText(selectedCategory);
            tvValueRow1.setTextColor(Color.BLACK);
        });
        builder.show();
    }

    private void showWalletDialog(boolean isDestinationWallet) {
        String[] wallets = new String[]{"Tiền mặt", "Vietcombank", "Momo", "Techcombank"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isDestinationWallet ? "Chọn ví đến" : "Chọn ví");
        builder.setItems(wallets, (dialog, which) -> {
            if(isDestinationWallet) {
                tvValueRow2.setText(wallets[which]); // Ví đích (cho chuyển khoản)
            } else {
                selectedWallet = wallets[which]; // Ví chính
                tvValueRow2.setText(selectedWallet);
            }
            tvValueRow2.setTextColor(Color.BLACK);
        });
        builder.show();
    }

    // --- 6. LƯU DỮ LIỆU ---
    private void saveTransaction() {
        String amountStr = edtAmount.getText().toString().trim();
        String note = edtGhiChu.getText().toString().trim();
        boolean isReport = !switchBaoCao.isChecked(); // Switch tắt -> Có báo cáo

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String typeFirebase = "CHI"; // Default

        // Logic map Tab sang Type Firebase
        // Lưu ý: Cần đảm bảo Model GiaoDich của bạn có trường để lưu các thông tin phụ này
        // Nếu Model cũ chưa có, tạm thời ta map vào field 'category' hoặc 'note'

        if (currentTabPosition == 0) typeFirebase = "CHI";
        else if (currentTabPosition == 1) typeFirebase = "THU";
        else if (currentTabPosition == 2) typeFirebase = debtType; // DiVay, ChoVay...
        else typeFirebase = "CHUYEN";

        // Validate cơ bản
        if (TextUtils.isEmpty(selectedCategory) && currentTabPosition != 3) {
            Toast.makeText(this, "Vui lòng chọn mục/người", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Object
        GiaoDich gd = new GiaoDich(amount, selectedCategory, note, selectedDate, typeFirebase);
        // Lưu ý: Nếu model GiaoDich chưa có trường 'wallet', bạn nên thêm vào hoặc nối chuỗi vào note
        // Ví dụ tạm thời nối vào Note nếu chưa update Model:
        gd.setNote(note + " | Ví: " + selectedWallet);

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        db.collection("users").document(uid).collection("transactions")
                .add(gd)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Đã thêm giao dịch!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Thêm giao dịch");
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}