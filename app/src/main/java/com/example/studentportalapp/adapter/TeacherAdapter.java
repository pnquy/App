package com.example.studentportalapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportalapp.data.TeacherItem;
import com.example.studentportalapp.R;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<TeacherItem> list;

    public TeacherAdapter(List<TeacherItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherItem item = list.get(position);
        holder.tvMaGV.setText("Mã GV: " + item.MaGV);
        holder.tvHoTen.setText("Họ tên: " + item.HoTen);
        holder.tvEmail.setText("Email: " + item.Email);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setData(List<TeacherItem> newList){
        this.list = newList;
        notifyDataSetChanged();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaGV, tvHoTen, tvEmail;
        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaGV = itemView.findViewById(R.id.tvMaGV);
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}
