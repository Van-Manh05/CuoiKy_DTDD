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

    private List<ViTien> mList;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    private OnWalletClickListener listener; // Khai báo listener

    // Interface để gửi sự kiện click ra ngoài Activity
    public interface OnWalletClickListener {
        void onWalletClick(ViTien viTien);
    }

    // Cập nhật Constructor để nhận listener
    public ViTienAdapter(List<ViTien> mList, OnWalletClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViTienViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false);
        return new ViTienViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViTienViewHolder holder, int position) {
        ViTien vi = mList.get(position);
        if (vi == null) return;

        holder.tvName.setText(vi.getName());
        holder.tvBalance.setText(formatter.format(vi.getBalance()) + " đ");

        // Logic chọn icon thông minh
        String nameLower = vi.getName().toLowerCase();
        if (nameLower.contains("momo")) {
            holder.imgIcon.setImageResource(R.drawable.ic_wallet_gray); // Bạn nên thêm icon momo nếu có
            // holder.imgIcon.setColorFilter(Color.parseColor("#A50064")); // Ví dụ đổi màu hồng
        } else if (nameLower.contains("zalo")) {
            holder.imgIcon.setImageResource(R.drawable.ic_wallet_gray);
            // holder.imgIcon.setColorFilter(Color.parseColor("#0068FF")); // Ví dụ đổi màu xanh
        } else if (nameLower.contains("ngân hàng") || nameLower.contains("bank")) {
            holder.imgIcon.setImageResource(R.drawable.ic_wallet_gray);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_wallet_gray);
        }

        // BẮT SỰ KIỆN CLICK
        holder.itemView.setOnClickListener(v -> listener.onWalletClick(vi));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class ViTienViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBalance;
        ImageView imgIcon;

        public ViTienViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvWalletName);
            tvBalance = itemView.findViewById(R.id.tvWalletBalance);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }
}