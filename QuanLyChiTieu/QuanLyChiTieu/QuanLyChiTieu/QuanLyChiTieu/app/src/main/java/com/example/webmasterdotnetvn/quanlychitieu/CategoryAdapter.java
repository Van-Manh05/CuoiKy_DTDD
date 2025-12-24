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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvName.setText(category.getName());
        holder.imgIcon.setImageResource(category.getIconResId());

        // Xử lý đổi màu nền hình tròn động theo mã màu Hex
        try {
            // Lấy drawable hình tròn (bg_circle_green hoặc file gốc màu trắng)
            // Bạn nên tạo file bg_circle_base.xml màu trắng để dễ tint màu
            GradientDrawable background = (GradientDrawable) holder.imgIcon.getBackground();
            if (background != null) {
                // Vì setTint thay đổi màu gốc, nên cần mutate() để không ảnh hưởng các item khác
                background.mutate();
                background.setColor(Color.parseColor(category.getColorHex()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvName;
        ImageView imgEdit;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
        }
    }
}