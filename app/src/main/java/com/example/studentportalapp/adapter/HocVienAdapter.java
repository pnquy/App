package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.R;
import com.example.studentportalapp.data.HocVien;

import java.util.List;

public class HocVienAdapter extends RecyclerView.Adapter<HocVienAdapter.ViewHolder> {

    private final List<HocVien> hocVienList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(HocVien hv);
        void onDelete(HocVien hv);
    }

    public HocVienAdapter(Context context, List<HocVien> list, OnItemClickListener listener) {
        this.context = context;
        this.hocVienList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hocvien, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HocVien hv = hocVienList.get(position);
        holder.tvTenHV.setText(hv.getHoTen());
        holder.tvEmailHV.setText("Email: " + hv.getEmail());
        holder.tvLopHV.setText("Lớp: " + hv.getMaLop());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(hv));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(hv));
    }

    @Override
    public int getItemCount() {
        return hocVienList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenHV, tvEmailHV, tvLopHV;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenHV = itemView.findViewById(R.id.tvHoTenHV);
            tvEmailHV = itemView.findViewById(R.id.tvEmailHV);
            tvLopHV = itemView.findViewById(R.id.tvMaLopHV);
            btnEdit = itemView.findViewById(R.id.btnEditHV);
            btnDelete = itemView.findViewById(R.id.btnDeleteHV);
        }
    }
}
