package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.model.TeacherDisplayItem;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {

    private Context context;
    private List<TeacherDisplayItem> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(TeacherDisplayItem item);
        void onDelete(TeacherDisplayItem item);
        void onViewClasses(TeacherDisplayItem item);
    }

    public TeacherAdapter(Context context, List<TeacherDisplayItem> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeacherDisplayItem item = list.get(position);

        holder.tvTen.setText(item.giaoVien.getTenGV());
        holder.tvMa.setText("MSGV: " + item.giaoVien.getMaGV());
        holder.tvEmail.setText(item.giaoVien.getEmail());
        holder.tvCount.setText(String.valueOf(item.classCount));

        // Sự kiện click 3 nút
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
        holder.btnViewClasses.setOnClickListener(v -> listener.onViewClasses(item));
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvMa, tvEmail, tvCount;
        TextView btnEdit, btnDelete, btnViewClasses;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTenGV);
            tvMa = itemView.findViewById(R.id.tvMaGV);
            tvEmail = itemView.findViewById(R.id.tvEmailGV);
            tvCount = itemView.findViewById(R.id.tvClassCount);

            btnEdit = itemView.findViewById(R.id.btnEditGV);
            btnDelete = itemView.findViewById(R.id.btnDeleteGV);
            btnViewClasses = itemView.findViewById(R.id.btnViewClasses);
        }
    }
}