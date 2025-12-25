// File: ChiPhiAdapter.java
package com.example.webmasterdotnetvn.quanlychitieu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Import để dùng màu
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class ChiPhiAdapter extends RecyclerView.Adapter<ChiPhiAdapter.ChiPhiViewHolder> {

    private List<ChiPhi> chiPhiList;
    private DecimalFormat decimalFormat;

    public ChiPhiAdapter(List<ChiPhi> chiPhiList) {
        this.chiPhiList = chiPhiList;

        this.decimalFormat = new DecimalFormat("#,###.###");
    }

    @NonNull
    @Override
    public ChiPhiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_nhom, parent, false);
        return new ChiPhiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChiPhiViewHolder holder, int position) {
        ChiPhi chiPhi = chiPhiList.get(position);

        holder.icon.setImageResource(chiPhi.getIconResId());
        holder.categoryName.setText(chiPhi.getTenNhom());
        holder.amount.setText(decimalFormat.format(chiPhi.getSoTien())); // Định dạng số tiền
        holder.percentageChange.setText(decimalFormat.format(chiPhi.getPhanTramThayDoi()));

        // Đổi màu và icon mũi tên dựa vào giá trị thay đổi
        if (chiPhi.getPhanTramThayDoi() >= 0) {
            // Dùng ContextCompat.getColor cho an toàn
            int redColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark);
            holder.percentageChange.setTextColor(redColor);
            holder.arrow.setImageResource(R.drawable.ic_arrow_up); // Mũi tên lên
        } else {
            int greenColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark);
            holder.percentageChange.setTextColor(greenColor);
            holder.arrow.setImageResource(R.drawable.ic_arrow_down); // Mũi tên xuống
        }
    }

    @Override
    public int getItemCount() {
        return chiPhiList.size();
    }

    // Lớp ViewHolder: Dùng để "giữ" các View của mỗi item
    public static class ChiPhiViewHolder extends RecyclerView.ViewHolder {

        // Khai báo các View có trong file item_transaction_nhom.xml
        ImageView icon;
        ImageView arrow;
        TextView categoryName;
        TextView amount;
        TextView percentageChange;

        public ChiPhiViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các View
            icon = itemView.findViewById(R.id.ivCategoryIcon);
            arrow = itemView.findViewById(R.id.ivArrow);
            categoryName = itemView.findViewById(R.id.tvCategoryName);
            amount = itemView.findViewById(R.id.tvAmount);
            percentageChange = itemView.findViewById(R.id.tvPercentageChange);
        }
    }
}