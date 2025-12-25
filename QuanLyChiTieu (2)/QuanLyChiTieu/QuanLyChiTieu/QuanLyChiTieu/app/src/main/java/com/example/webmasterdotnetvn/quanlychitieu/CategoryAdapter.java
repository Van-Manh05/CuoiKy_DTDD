package com.example.webmasterdotnetvn.quanlychitieu;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> mList;
    private ICategoryListener listener;


    public interface ICategoryListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public CategoryAdapter(List<Category> mList, ICategoryListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mList.get(position);
        if (category == null) return;

        
        holder.tvName.setText(category.getName());

        // 2. Xử lý sự kiện Click
        // Nút bút chì -> Sửa
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(category));

        // Nhấn giữ vào dòng -> Xóa
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDeleteClick(category);
            return true;
        });

        // 3. Tự động trang trí Icon và Màu sắc dựa trên tên
        configIconAndColor(holder, category);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // --- HÀM TRANG TRÍ GIAO DIỆN (Thay thế cho iconResId và colorHex) ---
    private void configIconAndColor(CategoryViewHolder holder, Category category) {
        String name = category.getName().toLowerCase();
        String type = category.getType(); // "CHI" hoặc "THU"

        int iconRes;
        String colorHex;

        // Logic chọn icon dựa trên từ khóa trong tên
        if (name.contains("ăn") || name.contains("thực phẩm")) {
            iconRes = R.drawable.ic_category_gray; // Thay bằng icon ăn uống nếu có
            colorHex = "#FFECB3"; // Vàng nhạt
        } else if (name.contains("nhà") || name.contains("thuê")) {
            iconRes = R.drawable.ic_category_gray; // Thay bằng icon nhà
            colorHex = "#FFCCBC"; // Cam nhạt
        } else if (name.contains("lương") || name.contains("thưởng")) {
            iconRes = R.drawable.ic_category_gray; // Thay bằng icon tiền
            colorHex = "#C8E6C9"; // Xanh lá nhạt
        } else if (name.contains("di chuyển") || name.contains("xăng") || name.contains("xe")) {
            iconRes = R.drawable.ic_category_gray;
            colorHex = "#B3E5FC"; // Xanh dương nhạt
        } else if (name.contains("mua")) {
            iconRes = R.drawable.ic_category_gray;
            colorHex = "#E1BEE7"; // Tím nhạt
        } else {
            // Mặc định
            iconRes = R.drawable.ic_category_gray;
            if ("THU".equals(type)) {
                colorHex = "#E0F2F1"; // Xanh ngọc nhạt cho Thu
            } else {
                colorHex = "#F5F5F5"; // Xám nhạt cho Chi
            }
        }

        // Set Icon (Nếu bạn có nhiều icon thì thay thế ở trên)
        // Hiện tại đang dùng chung ic_category_gray để tránh lỗi thiếu file
        holder.imgIcon.setImageResource(iconRes);

        // Set Màu Nền hình tròn
        try {
            GradientDrawable background = (GradientDrawable) holder.imgIcon.getBackground();
            if (background != null) {
                background.mutate();
                background.setColor(Color.parseColor(colorHex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgIcon, btnEdit;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ đúng ID trong file item_category.xml
            tvName = itemView.findViewById(R.id.tvCategoryName);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}