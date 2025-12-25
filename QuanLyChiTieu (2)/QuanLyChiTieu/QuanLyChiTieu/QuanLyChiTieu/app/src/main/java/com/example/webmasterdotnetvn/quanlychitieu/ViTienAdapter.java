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
    private OnWalletClickListener listener;

    public interface OnWalletClickListener {
        void onWalletClick(ViTien viTien);
        void onWalletLongClick(ViTien viTien);
    }

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

        // Logic Icon
        String nameLower = vi.getName().toLowerCase();
        if (nameLower.contains("ngân hàng") || nameLower.contains("bank")) {
            holder.imgIcon.setImageResource(R.drawable.ic_wallet_gray);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_wallet_gray);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onWalletClick(vi);
        });

        // Nhấn giữ (Long Click) -> Xóa
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onWalletLongClick(vi);
                return true; // Đã xử lý sự kiện
            }
            return false;
        });
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