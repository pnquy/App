package com.example.studentportalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SimpleLineChart extends View {
    private List<Double> scores = new ArrayList<>();
    private Paint linePaint, dotPaint, textPaint;

    public SimpleLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#2196F3")); // Màu xanh dương
        linePaint.setStrokeWidth(8f);
        linePaint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setColor(Color.parseColor("#E91E63")); // Màu hồng chấm tròn
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
        invalidate(); // Vẽ lại
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (scores == null || scores.isEmpty()) return;

        float padding = 50f;
        float width = getWidth() - 2 * padding;
        float height = getHeight() - 2 * padding;

        // Khoảng cách giữa các điểm trên trục X
        float xStep = (scores.size() > 1) ? width / (scores.size() - 1) : width / 2;

        for (int i = 0; i < scores.size(); i++) {
            float score = scores.get(i).floatValue();

            // Tính tọa độ (Điểm cao thì y nhỏ, điểm thấp thì y lớn)
            float x = padding + i * xStep;
            float y = getHeight() - padding - (score / 10f) * height;

            // Vẽ đường nối đến điểm tiếp theo
            if (i < scores.size() - 1) {
                float nextScore = scores.get(i + 1).floatValue();
                float nextX = padding + (i + 1) * xStep;
                float nextY = getHeight() - padding - (nextScore / 10f) * height;
                canvas.drawLine(x, y, nextX, nextY, linePaint);
            }

            // Vẽ chấm tròn
            canvas.drawCircle(x, y, 12f, dotPaint);
            // Vẽ điểm số trên đầu
            canvas.drawText(String.valueOf(score), x, y - 20, textPaint);
        }
    }
}