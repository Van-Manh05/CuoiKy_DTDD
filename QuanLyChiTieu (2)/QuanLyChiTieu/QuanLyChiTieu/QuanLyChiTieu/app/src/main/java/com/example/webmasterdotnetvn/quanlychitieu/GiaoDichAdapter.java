package com.example.webmasterdotnetvn.quanlychitieu;

import android.content.Context;
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

public class GiaoDichAdapter extends RecyclerView.Adapter<GiaoDichAdapter.GiaoDichViewHolder> {

    private Context context;
    private List<GiaoDich> list;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public GiaoDichAdapter(Context context, List<GiaoDich> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GiaoDichViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_giao_dich, parent, false);
        return new GiaoDichViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GiaoDichViewHolder holder, int position) {
        GiaoDich gd = list.get(position);

        holder.tvCategory.setText(gd.getCategory());
        holder.tvDate.setText(sdf.format(gd.getDate()));

        if ("THU".equals(gd.getType())) {
            holder.tvAmount.setText("+ " + formatter.format(gd.getAmount()) + " đ");
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh
            // holder.imgCategory.setImageResource(R.drawable.ic_income); // Thay icon nếu muốn
        } else {
            holder.tvAmount.setText("- " + formatter.format(gd.getAmount()) + " đ");
            holder.tvAmount.setTextColor(Color.parseColor("#E53935")); // Màu đỏ
        }


        holder.itemView.setOnClickListener(v -> {
            
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GiaoDichViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvAmount;
        ImageView imgCategory;

        public GiaoDichViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
}