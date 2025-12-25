package com.example.studentportalapp;

import android.content.Context; import android.graphics.Canvas; import android.graphics.Color; import android.graphics.Paint; import android.graphics.Rect; import android.util.AttributeSet; import android.view.View;

import androidx.annotation.Nullable;

import com.example.studentportalapp.model.StatsAssignmentItem;

import java.util.ArrayList; import java.util.List;

public class SimpleBarChart extends View {

    private List<StatsAssignmentItem> data = new ArrayList<>();
    private Paint barPaint;
    private Paint textPaint;
    private Paint axisPaint;
    private float maxScore = 10f;

    public SimpleBarChart(Context context) {
        super(context);
        init();
    }

    public SimpleBarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#4CAF50"));
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        axisPaint = new Paint();
        axisPaint.setColor(Color.GRAY);
        axisPaint.setStrokeWidth(5f);
    }

    public void setData(List<StatsAssignmentItem> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.isEmpty()) return;

        float padding = 50f;
        float chartWidth = getWidth() - 2 * padding;
        float chartHeight = getHeight() - 2 * padding;
        float barWidth = chartWidth / (data.size() * 2);
        float spacing = barWidth;

        canvas.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding, axisPaint);
        canvas.drawLine(padding, getHeight() - padding, padding, padding, axisPaint);

        for (int i = 0; i < data.size(); i++) {
            StatsAssignmentItem item = data.get(i);
            float score = (float) item.averageScore;

            float barHeight = (score / maxScore) * chartHeight;
            float left = padding + spacing + i * (barWidth + spacing);
            float top = (getHeight() - padding) - barHeight;
            float right = left + barWidth;
            float bottom = getHeight() - padding;

            if (score >= 8.0) barPaint.setColor(Color.parseColor("#4CAF50"));
            else if (score >= 5.0) barPaint.setColor(Color.parseColor("#FF9800"));
            else barPaint.setColor(Color.parseColor("#F44336"));

            canvas.drawRect(left, top, right, bottom, barPaint);

            String scoreText = String.format("%.1f", score);
            canvas.drawText(scoreText, left + barWidth / 2, top - 10, textPaint);

            String label = "BT" + (i + 1);
            canvas.drawText(label, left + barWidth / 2, bottom + 40, textPaint);
        }
    }
}