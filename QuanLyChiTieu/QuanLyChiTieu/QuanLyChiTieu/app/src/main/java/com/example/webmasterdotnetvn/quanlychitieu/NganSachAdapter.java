package com.example.webmasterdotnetvn.quanlychitieu;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class NganSachAdapter extends RecyclerView.Adapter<NganSachAdapter.BudgetViewHolder> {

    private List<NganSach> mList;
    private DecimalFormat formatter = new DecimalFormat("#,###");

    public NganSachAdapter(List<NganSach> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ngan_sach, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        NganSach budget = mList.get(position);
        if (budget == null) return;

        holder.tvCategoryName.setText(budget.getCategory());
        holder.tvSpent.setText(formatter.format(budget.getSpentAmount()) + " đ");
        holder.tvLimit.setText(formatter.format(budget.getLimitAmount()) + " đ");

        // Tính toán %
        double percent = 0;
        if (budget.getLimitAmount() > 0) {
            percent = (budget.getSpentAmount() / budget.getLimitAmount()) * 100;
        }
        int progress = (int) percent;
        holder.progressBar.setProgress(progress);
        holder.tvPercent.setText(progress + "%");

        double remaining = budget.getLimitAmount() - budget.getSpentAmount();
        holder.tvRemaining.setText("Còn lại: " + formatter.format(remaining) + " đ");

        // Xử lý màu sắc
        if (progress >= 100) {
            holder.progressBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            holder.tvStatus.setText("Vượt quá hạn mức!");
            holder.tvStatus.setTextColor(Color.RED);
            holder.tvPercent.setTextColor(Color.RED);
        } else if (progress >= 80) {
            holder.progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#FF9800"), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.tvStatus.setText("Sắp hết hạn mức");
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
            holder.tvPercent.setTextColor(Color.parseColor("#FF9800"));
        } else {
            holder.progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#4CAF50"), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.tvStatus.setText("Chi tiêu an toàn");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvPercent.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvStatus, tvPercent, tvSpent, tvLimit, tvRemaining;
        ProgressBar progressBar;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPercent = itemView.findViewById(R.id.tvPercent);
            tvSpent = itemView.findViewById(R.id.tvSpent);
            tvLimit = itemView.findViewById(R.id.tvLimit);
            tvRemaining = itemView.findViewById(R.id.tvRemaining);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}