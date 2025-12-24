// File: ViTienAdapter.java
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

public class ViTienAdapter extends RecyclerView.Adapter<ViTienAdapter.ViTienViewHolder> {

    private List<ViTien> viTienList;
    private DecimalFormat decimalFormat;

    public ViTienAdapter(List<ViTien> viTienList) {
        this.viTienList = viTienList;
        this.decimalFormat = new DecimalFormat("#,###.###");
    }

    @NonNull
    @Override
    public ViTienViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vi_tien, parent, false);
        return new ViTienViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViTienViewHolder holder, int position) {
        ViTien viTien = viTienList.get(position);

        holder.icon.setImageResource(viTien.getIconResId());
        holder.tenVi.setText(viTien.getTenVi());
        holder.soTien.setText(decimalFormat.format(viTien.getSoTien()));
    }

    @Override
    public int getItemCount() {
        return viTienList.size();
    }

    public static class ViTienViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tenVi;
        TextView soTien;
        ImageView ivMore; // Nút 3 chấm (để đó, chưa xử lý)

        public ViTienViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivViIcon);
            tenVi = itemView.findViewById(R.id.tvTenVi);
            soTien = itemView.findViewById(R.id.tvSoTien);
            ivMore = itemView.findViewById(R.id.ivMore);
        }
    }
}