package com.example.studentportalapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.model.ClassDisplayItem; // Class mới tạo
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<ClassDisplayItem> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAddStudent(ClassDisplayItem item);
        void onStats(ClassDisplayItem item);
        void onEdit(ClassDisplayItem item);
        void onDelete(ClassDisplayItem item);
    }

    public ClassAdapter(List<ClassDisplayItem> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassDisplayItem item = list.get(position);

        holder.tvMaLH.setText(item.lopHoc.MaLH);
        holder.tvTenLH.setText(item.lopHoc.TenLH);
        holder.tvTenGV.setText("GVCN: " + (item.tenGV != null ? item.tenGV : "Chưa có"));
        holder.tvSiSo.setText("Sĩ số: " + item.siSo);

        // Gán sự kiện cho 4 nút
        holder.btnAddStudent.setOnClickListener(v -> listener.onAddStudent(item));
        holder.btnStats.setOnClickListener(v -> listener.onStats(item));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaLH, tvTenLH, tvTenGV, tvSiSo;
        ImageView btnAddStudent, btnStats, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaLH = itemView.findViewById(R.id.tvMaLH);
            tvTenLH = itemView.findViewById(R.id.tvTenLH);
            tvTenGV = itemView.findViewById(R.id.tvTenGV);
            tvSiSo = itemView.findViewById(R.id.tvSiSo);

            btnAddStudent = itemView.findViewById(R.id.btnAddStudent);
            btnStats = itemView.findViewById(R.id.btnStats);
            btnEdit = itemView.findViewById(R.id.btnEditClass);
            btnDelete = itemView.findViewById(R.id.btnDeleteClass);
        }
    }
}