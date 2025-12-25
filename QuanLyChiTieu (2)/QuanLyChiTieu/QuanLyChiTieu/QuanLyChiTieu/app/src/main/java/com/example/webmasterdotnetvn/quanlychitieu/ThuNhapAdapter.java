
package com.example.webmasterdotnetvn.quanlychitieu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class ThuNhapAdapter extends RecyclerView.Adapter<ThuNhapAdapter.ThuNhapViewHolder> {

    private List<ThuNhap> thuNhapList;
    private DecimalFormat decimalFormat;

    public ThuNhapAdapter(List<ThuNhap> thuNhapList) {
        this.thuNhapList = thuNhapList;
        this.decimalFormat = new DecimalFormat("#,###.###");
    }

    @NonNull
    @Override
    public ThuNhapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thu_nhap, parent, false);
        return new ThuNhapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThuNhapViewHolder holder, int position) {
        ThuNhap thuNhap = thuNhapList.get(position);

        holder.icon.setImageResource(thuNhap.getIconResId());
        holder.categoryName.setText(thuNhap.getTenNguon());
        holder.amount.setText(decimalFormat.format(thuNhap.getSoTien()));
    }

    @Override
    public int getItemCount() {
        return thuNhapList.size();
    }

    public static class ThuNhapViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView categoryName;
        TextView amount; // tien

        public ThuNhapViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivCategoryIcon);
            categoryName = itemView.findViewById(R.id.tvCategoryName);
            amount = itemView.findViewById(R.id.tvAmount);
        }
    }
}