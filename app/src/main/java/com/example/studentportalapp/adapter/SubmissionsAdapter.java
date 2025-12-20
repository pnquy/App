package com.example.studentportalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentportalapp.R;
import com.example.studentportalapp.data.AppDatabase;
import com.example.studentportalapp.data.Entity.HocVien;
import com.example.studentportalapp.data.Entity.NopBai;
import java.util.List;
import java.util.concurrent.Executors;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.ViewHolder> {

    private final Context context;
    private final List<NopBai> list;
    private final OnItemClickListener listener;
    private final AppDatabase db;

    public interface OnItemClickListener {
        void onItemClick(NopBai nb);
    }

    public SubmissionsAdapter(Context context, List<NopBai> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.db = AppDatabase.getDatabase(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NopBai nb = list.get(position);

        holder.tvId.setText("Mã HV: " + nb.MaHV);
        holder.tvDate.setText(nb.NgayNop);
        holder.tvNote.setText("Ghi chú: " + (nb.GhiChu != null && !nb.GhiChu.isEmpty() ? nb.GhiChu : "(Không có ghi chú)"));

        if (nb.FileName != null && !nb.FileName.isEmpty()) {
            holder.tvFileName.setText(nb.FileName);
            holder.layoutFile.setVisibility(View.VISIBLE);
        } else {
            holder.layoutFile.setVisibility(View.GONE);
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            HocVien hv = db.hocVienDao().getByIdSync(nb.MaHV);
            if (hv != null) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        holder.tvName.setText(hv.getTenHV());
                    });
                }
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(nb));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvNote, tvName, tvFileName;
        View layoutFile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_submitter_name);
            tvId = itemView.findViewById(R.id.tv_submitter_id);
            tvDate = itemView.findViewById(R.id.tv_submit_date);
            tvNote = itemView.findViewById(R.id.tv_submit_note);
            tvFileName = itemView.findViewById(R.id.tv_submission_file_name);
            layoutFile = itemView.findViewById(R.id.layout_submission_file);
        }
    }
}