package com.example.webmasterdotnetvn.quanlychitieu;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LichSuAdapter extends RecyclerView.Adapter<LichSuAdapter.HistoryViewHolder> {

    private List<GiaoDich> mList;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public LichSuAdapter(List<GiaoDich> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_transaction, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        GiaoDich gd = mList.get(position);
        if (gd == null) return;

        // 1. Hiển thị tên (Category hoặc Note)
        if (gd.getCategory() != null && !gd.getCategory().isEmpty()) {
            holder.tvCategory.setText(gd.getCategory());
        } else {
            holder.tvCategory.setText(gd.getNote());
        }

        // 2. Hiển thị ngày
        if (gd.getDate() != null) {
            holder.tvDate.setText(sdf.format(gd.getDate()));
        }

        // 3. Xử lý màu sắc và dấu +/-
        if ("THU".equals(gd.getType()) || "Nạp tiền".equals(gd.getCategory())) {
            // Tiền vào: Màu xanh
            holder.tvAmount.setText("+ " + formatter.format(gd.getAmount()) + " đ");
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.imgIcon.setImageResource(android.R.drawable.stat_sys_download); // Icon xuống
        } else {
            // Tiền ra: Màu đỏ
            holder.tvAmount.setText("- " + formatter.format(gd.getAmount()) + " đ");
            holder.tvAmount.setTextColor(Color.parseColor("#F44336")); // Red
            holder.imgIcon.setImageResource(android.R.drawable.stat_sys_upload); // Icon lên
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvAmount;
        ImageView imgIcon;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            imgIcon = itemView.findViewById(R.id.imgCategory);
        }
    }
}