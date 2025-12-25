package com.example.webmasterdotnetvn.quanlychitieu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryListFragment extends Fragment {

    private RecyclerView rcvCategory;
    private FloatingActionButton fabAdd;

    private CategoryAdapter adapter;
    private List<Category> mListCategory;

    private FirebaseFirestore db;
    private String uid;

    private int typeInt; 
    private String typeString;

    // Phương thức tạo Fragment với t số
    public static CategoryListFragment newInstance(int type) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putInt("TYPE", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Lưu ý: Layout này cần có RecyclerView và FloatingActionButton
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        // 1. Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // 2. Nhận dữ liệu loại (Chi/Thu)
        if (getArguments() != null) {
            typeInt = getArguments().getInt("TYPE");
            // Chuyển đổi số sang chuỗi để lưu vào DB
            typeString = (typeInt == 0) ? "CHI" : "THU";
        }

        // 3. Ánh xạ View
        // (Đảm bảo ID trong file fragment_category_list.xml khớp với code này)
        rcvCategory = view.findViewById(R.id.rcvCategory);
        fabAdd = view.findViewById(R.id.fabAddCategory);

        // 4. Thiết lập RecyclerView
        setupRecyclerView();

        // 5. Tải dữ liệu từ Firebase
        loadDataFromFirebase();

        // 6. Sự kiện bấm nút Thêm
        fabAdd.setOnClickListener(v -> showAddDialog());

        return view;
    }

    private void setupRecyclerView() {
        mListCategory = new ArrayList<>();

        // Khởi tạo Adapter và lắng nghe sự kiện Sửa/Xóa từ Adapter
        adapter = new CategoryAdapter(mListCategory, new CategoryAdapter.ICategoryListener() {
            @Override
            public void onEditClick(Category category) {
                showEditDialog(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                showDeleteDialog(category);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvCategory.setLayoutManager(linearLayoutManager);

        // Thêm đường kẻ ngang giữa các dòng cho đẹp
        rcvCategory.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        rcvCategory.setAdapter(adapter);
    }

    private void loadDataFromFirebase() {
        if (uid == null) return;

        // Lọc dữ liệu theo "type" (CHI hoặc THU)
        db.collection("users").document(uid).collection("categories")
                .whereEqualTo("type", typeString)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        mListCategory.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Category cat = doc.toObject(Category.class);
                            if (cat != null) {
                                cat.setId(doc.getId()); // Lưu ID để sau này Sửa/Xóa
                                mListCategory.add(cat);
                            }
                        }

                        // Nếu danh sách trống (lần đầu dùng), tự tạo mẫu
                        if (mListCategory.isEmpty()) {
                            createDefaultCategories();
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void createDefaultCategories() {
        // Tự động tạo vài mục mẫu nếu người dùng chưa có gì
        if (typeInt == 0) { // CHI
            addCategoryToFirebase("Ăn uống");
            addCategoryToFirebase("Mua sắm");
            addCategoryToFirebase("Di chuyển");
            addCategoryToFirebase("Nhà cửa");
        } else { // THU
            addCategoryToFirebase("Lương");
            addCategoryToFirebase("Thưởng");
            addCategoryToFirebase("Bán đồ");
        }
    }

    // --- CÁC HÀM XỬ LÝ THÊM / SỬA / XÓA ---

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm mục " + (typeInt == 0 ? "Chi" : "Thu"));

        final EditText input = new EditText(getContext());
        input.setHint("Nhập tên danh mục");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                addCategoryToFirebase(name);
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showEditDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa tên danh mục");

        final EditText input = new EditText(getContext());
        input.setText(category.getName());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                // Update lên Firebase
                db.collection("users").document(uid).collection("categories")
                        .document(category.getId())
                        .update("name", newName)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show());
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showDeleteDialog(Category category) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa danh mục")
                .setMessage("Bạn chắc chắn muốn xóa '" + category.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Delete trên Firebase
                    db.collection("users").document(uid).collection("categories")
                            .document(category.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addCategoryToFirebase(String name) {
        Map<String, Object> category = new HashMap<>();
        category.put("name", name);
        category.put("type", typeString); // Lưu đúng loại (CHI/THU)

        db.collection("users").document(uid).collection("categories")
                .add(category);
    }
}