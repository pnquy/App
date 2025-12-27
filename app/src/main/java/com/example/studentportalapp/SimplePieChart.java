package com.example.studentportalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class SimplePieChart extends View {
    private int valueStudent = 0;
    private int valueTeacher = 0;

    private Paint paintStudent;
    private Paint paintTeacher;
    private RectF rectF;

    public SimplePieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintStudent = new Paint();
        paintStudent.setColor(Color.parseColor("#4CAF50")); // Màu xanh lá (Học viên)
        paintStudent.setAntiAlias(true);
        paintStudent.setStyle(Paint.Style.FILL);

        paintTeacher = new Paint();
        paintTeacher.setColor(Color.parseColor("#FF9800")); // Màu cam (Giáo viên)
        paintTeacher.setAntiAlias(true);
        paintTeacher.setStyle(Paint.Style.FILL);
    }

    public void setData(int students, int teachers) {
        this.valueStudent = students;
        this.valueTeacher = teachers;
        invalidate(); // Vẽ lại
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int total = valueStudent + valueTeacher;
        if (total == 0) return;

        float width = getWidth();
        float height = getHeight();
        float diameter = Math.min(width, height) * 0.8f; // Kích thước biểu đồ

        // Căn giữa
        float left = (width - diameter) / 2;
        float top = (height - diameter) / 2;

        if (rectF == null) {
            rectF = new RectF(left, top, left + diameter, top + diameter);
        }

        // Tính góc
        float angleStudent = (float) valueStudent / total * 360;
        float angleTeacher = 360 - angleStudent;

        // Vẽ cung tròn Học viên
        canvas.drawArc(rectF, -90, angleStudent, true, paintStudent);

        // Vẽ cung tròn Giáo viên
        canvas.drawArc(rectF, -90 + angleStudent, angleTeacher, true, paintTeacher);
    }
}