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
import com.example.studentportalapp.model.StudentItem; // Nhớ import file vừa tạo

import java.util.List;

public class HocVienAdapter extends RecyclerView.Adapter<HocVienAdapter.ViewHolder> {

    private Context context;
    private List<StudentItem> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(com.example.studentportalapp.data.Entity.HocVien hv);
        void onDelete(com.example.studentportalapp.data.Entity.HocVien hv);
    }

    public HocVienAdapter(Context context, List<StudentItem> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
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
        StudentItem item = list.get(position);

        holder.tvTen.setText(item.hocVien.getTenHV());
        holder.tvEmail.setText(item.hocVien.getEmail());

        if (item.classNames == null || item.classNames.isEmpty()) {
            holder.tvLop.setText("Lớp: Chưa xếp lớp");
        } else {
            holder.tvLop.setText("Lớp: " + item.classNames);
        }

        // Sự kiện click
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item.hocVien));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item.hocVien));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvEmail, tvLop;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvHoTenHV);
            tvEmail = itemView.findViewById(R.id.tvEmailHV);
            tvLop = itemView.findViewById(R.id.tvMaLopHV);
            btnEdit = itemView.findViewById(R.id.btnEditHV);
            btnDelete = itemView.findViewById(R.id.btnDeleteHV);
        }
    }
}