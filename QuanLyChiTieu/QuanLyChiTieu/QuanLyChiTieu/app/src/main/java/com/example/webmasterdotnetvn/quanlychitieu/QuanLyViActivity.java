package com.example.webmasterdotnetvn.quanlychitieu;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuanLyViActivity extends AppCompatActivity {

    private ImageView btnBack, btnAddWallet;
    private ListView lvWallets;

    // Sử dụng Adapter tùy chỉnh thay vì ArrayAdapter mặc định
    private WalletAdapter adapter;
    private List<WalletModel> walletList;

    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_vi);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        btnAddWallet = findViewById(R.id.btnAddWallet);
        lvWallets = findViewById(R.id.lvWallets);

        // Khởi tạo List và Adapter
        walletList = new ArrayList<>();
        adapter = new WalletAdapter();
        lvWallets.setAdapter(adapter);

        // Load dữ liệu
        loadWallets();

        // Sự kiện Click
        btnBack.setOnClickListener(v -> finish());
        btnAddWallet.setOnClickListener(v -> showAddWalletDialog());

        // Nhấn giữ để xóa
        lvWallets.setOnItemLongClickListener((parent, view, position, id) -> {
            WalletModel wallet = walletList.get(position);
            showDeleteDialog(wallet);
            return true;
        });
    }

    private void loadWallets() {
        if (uid == null) return;

        db.collection("users").document(uid).collection("wallets")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        walletList.clear();

                        // Kiểm tra nếu chưa có ví nào -> Tạo ví mặc định
                        if (value.isEmpty()) {
                            createDefaultWallets();
                            return;
                        }

                        // Lấy dữ liệu từ Firebase về
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String name = doc.getString("name");
                            Double balance = doc.getDouble("balance");
                            if (balance == null) balance = 0.0;

                            if (name != null) {
                                walletList.add(new WalletModel(doc.getId(), name, balance));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    // --- HÀM TẠO DỮ LIỆU MẶC ĐỊNH (CHẠY KHI LIST TRỐNG) ---
    private void createDefaultWallets() {
        // Tạo 3 ví cơ bản
        addWalletToFirebase("Tiền mặt", 0);
        addWalletToFirebase("Ngân hàng", 0);
        addWalletToFirebase("Ví điện tử", 0);
        Toast.makeText(this, "Đã khởi tạo các ví mặc định", Toast.LENGTH_SHORT).show();
    }

    private void showAddWalletDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm ví mới");

        final EditText input = new EditText(this);
        input.setHint("Nhập tên ví (VD: Heo đất)");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String walletName = input.getText().toString().trim();
            if (!walletName.isEmpty()) {
                addWalletToFirebase(walletName, 0);
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void addWalletToFirebase(String name, double balance) {
        Map<String, Object> wallet = new HashMap<>();
        wallet.put("name", name);
        wallet.put("balance", balance);

        db.collection("users").document(uid).collection("wallets").add(wallet);
    }

    private void showDeleteDialog(WalletModel wallet) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa ví")
                .setMessage("Bạn muốn xóa ví '" + wallet.name + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.collection("users").document(uid).collection("wallets").document(wallet.id).delete();
                    Toast.makeText(this, "Đã xóa ví", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // --- CLASS MODEL (Lưu cấu trúc 1 cái ví) ---
    class WalletModel {
        String id;
        String name;
        double balance;

        public WalletModel(String id, String name, double balance) {
            this.id = id;
            this.name = name;
            this.balance = balance;
        }
    }

    // --- CLASS ADAPTER (Để hiển thị giao diện tùy chỉnh item_wallet.xml) ---
    class WalletAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return walletList.size();
        }

        @Override
        public Object getItem(int position) {
            return walletList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(QuanLyViActivity.this)
                        .inflate(R.layout.item_wallet, parent, false);
            }

            // Ánh xạ view trong item_wallet.xml
            TextView tvName = convertView.findViewById(R.id.tvWalletName);
            TextView tvBalance = convertView.findViewById(R.id.tvWalletBalance);

            // Gán dữ liệu
            WalletModel wallet = walletList.get(position);
            tvName.setText(wallet.name);

            // Format tiền tệ (Ví dụ: 1,000,000 đ)
            DecimalFormat formatter = new DecimalFormat("#,###");
            tvBalance.setText(formatter.format(wallet.balance) + " đ");

            return convertView;
        }
    }
}