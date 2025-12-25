package com.example.studentportalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.AssignmentActivity;
import com.example.studentportalapp.GradeActivity;
import com.example.studentportalapp.R;
import com.example.studentportalapp.ViewSubmissionsActivity;
import com.example.studentportalapp.data.Entity.ThongBao;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<ThongBao> list;

    public NotificationAdapter(List<ThongBao> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list == null || position >= list.size()) return;
        
        ThongBao tb = list.get(position);
        if (tb == null) return;

        holder.tvContent.setText(tb.NoiDung != null ? tb.NoiDung : "");
        holder.tvDate.setText(tb.NgayTao != null ? tb.NgayTao : "");
        
        if (tb.IsRead) {
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            try {
                if (tb.LoaiTB == null || tb.LoaiTB.isEmpty()) {
                    return;
                }

                Intent intent = null;
                switch (tb.LoaiTB) {
                    case "ASSIGNMENT":
                    case "LECTURE":
                        if (tb.TargetId != null) {
                            SharedPreferences prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("CURRENT_CLASS_ID", tb.TargetId);
                            editor.apply();
                            intent = new Intent(context, AssignmentActivity.class);
                        }
                        break;
                    case "GRADE":
                        intent = new Intent(context, GradeActivity.class);
                        break;
                    case "SUBMISSION":
                        if (tb.TargetId != null) {
                            intent = new Intent(context, ViewSubmissionsActivity.class);
                            intent.putExtra("MA_BT", tb.TargetId);
                            intent.putExtra("TEN_BT", "Bài tập liên quan");
                        }
                        break;
                }

                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_noti_content);
            tvDate = itemView.findViewById(R.id.tv_noti_date);
        }
    }
}
