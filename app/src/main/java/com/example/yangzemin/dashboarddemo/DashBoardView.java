package com.example.yangzemin.dashboarddemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by yangzemin on 2017/5/9.
 */

public class DashBoardView extends View {

    private int padding;
    private float startAngle;
    private float sweepAngle;
    private PointF centerPoint;

    //最外层线
    private int outerNums = 3;
    private float mOuterRadius;
    private int[] outerLineColors;
    private float outerLineWidth;
    private RectF outerRecf;
    private Paint outerPaint;

    //中间彩带
    private float stripeRadius;
    private float stripeLineWidth;
    private RectF stripeRecf;
    private Paint stripePaint;

    //中心circle
    private float centerCircleRadius;
    private float centerCircleWidth;
    private RectF centerCircleRecf;
    private Paint centerCirclePaint;


    //刻度
    private int measuresCount;
    private float mBigSliceRadius;
    private float mSmallSliceRadius;
    private float mTextRadius;
    private Paint measuresPaint;
    private Paint textPaint;
    private Rect mTextMeasures;

    //指针
    private Path pointerPath;
    private float pointerRadius;
    private PointF pointerStartPointf;
    private PointF startLeftPoint;
    private PointF startRightPoint;
    private PointF stopLeftPoint;
    private PointF stopRightPoint;
    private Paint pointerPaint;
    private float pointerStartRadius, pointerStopRadius;

    private float minValue, maxValue, currentValue;
    private float currentValueAngle;

    public DashBoardView(Context context) {
        this(context, null);
    }

    public DashBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        padding = dpToPx(8);

        startAngle = 180;
        sweepAngle = 180;
        measuresCount = 18;

        centerCircleWidth = dpToPx(4);
        mBigSliceRadius = dpToPx(16);
        mSmallSliceRadius = dpToPx(8);

        pointerStartRadius = dpToPx(2);
        pointerStopRadius = dpToPx(3);

        mTextMeasures = new Rect();

        pointerPath = new Path();
        currentValueAngle = 0;

        minValue = 0;
        maxValue = 180;

        outerLineColors = new int[]{Color.parseColor("#99cff9"), Color.parseColor("#4695d5"), Color.parseColor("#ee9450")};
        outerLineWidth = dpToPx(8);
        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeWidth(outerLineWidth);
        //paint.setStrokeCap(Paint.Cap.ROUND);

        stripePaint = new Paint();
        stripePaint.setAntiAlias(true);
        stripePaint.setStyle(Paint.Style.STROKE);

        centerCirclePaint = new Paint();
        centerCirclePaint.setAntiAlias(true);
        centerCirclePaint.setStyle(Paint.Style.STROKE);
        centerCirclePaint.setColor(Color.parseColor("#2397f3"));
        centerCirclePaint.setStrokeWidth(centerCircleWidth);

        measuresPaint = new Paint();
        measuresPaint.setAntiAlias(true);
        measuresPaint.setStyle(Paint.Style.STROKE);
        measuresPaint.setColor(Color.parseColor("#a1a1a1"));
        measuresPaint.setStrokeWidth(dpToPx(4));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.parseColor("#a1a1a1"));
        textPaint.setStrokeWidth(dpToPx(2));
        textPaint.setTextSize(spToPx(14));

        pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setColor(Color.parseColor("#f3663e"));
        pointerPaint.setStrokeWidth(dpToPx(4));
        pointerPaint.setTextSize(spToPx(14));

    }

    private void initPointer() {

        pointerStartPointf = getCoordinatePoint(pointerRadius, startAngle + currentValueAngle);

        startLeftPoint = getCoordinatePoint(pointerStartPointf, pointerStartRadius, startAngle + currentValueAngle - 90);
        startRightPoint = getCoordinatePoint(pointerStartPointf, pointerStartRadius, startAngle + currentValueAngle + 90);

        stopLeftPoint = getCoordinatePoint(pointerStopRadius, startAngle + currentValueAngle - 90);
        stopRightPoint = getCoordinatePoint(pointerStopRadius, startAngle + currentValueAngle + 90);

        pointerPath = new Path();
        pointerPath.moveTo(stopLeftPoint.x, stopLeftPoint.y);
        pointerPath.lineTo(stopRightPoint.x, stopRightPoint.y);
        pointerPath.lineTo(startRightPoint.x, startRightPoint.y);
        pointerPath.lineTo(startLeftPoint.x, startLeftPoint.y);
        pointerPath.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawOuter(canvas);
        drawStripe(canvas);
        drawCenterCircle(canvas);
        drawMeasures(canvas);
        drawPointer(canvas);

    }

    private void drawPointer(Canvas canvas) {
        canvas.drawPath(pointerPath, pointerPaint);

        canvas.drawCircle(pointerStartPointf.x, pointerStartPointf.y, pointerStartRadius, pointerPaint);
        canvas.drawCircle(centerPoint.x, centerPoint.y, pointerStopRadius, pointerPaint);

    }

    /**
     * 绘制刻度
     */
    private void drawMeasures(Canvas canvas) {

        float perAngle = sweepAngle / measuresCount;
        float sliceRadius;

        for (int i = 0; i <= measuresCount; i++) {

            if (i % 2 == 0) {
                sliceRadius = stripeRadius + mBigSliceRadius;
                drawText(canvas, i, perAngle);
            } else {
                sliceRadius = stripeRadius + mSmallSliceRadius;
            }

            PointF startPointf = getCoordinatePoint(sliceRadius, startAngle + i * perAngle);
            PointF stopPointf = getCoordinatePoint(stripeRadius, startAngle + i * perAngle);

            PointF centerStartPointF = getCoordinatePoint(centerCircleRadius + centerCircleWidth / 2, startAngle + i * perAngle);
            PointF centerStopPointF = getCoordinatePoint(centerCircleRadius - dpToPx(8), startAngle + i * perAngle);

            canvas.drawLine(startPointf.x, startPointf.y, stopPointf.x, stopPointf.y, measuresPaint);
            canvas.drawLine(centerStartPointF.x, centerStartPointF.y, centerStopPointF.x, centerStopPointF.y, centerCirclePaint);
        }


    }

    private void drawText(Canvas canvas, int index, float perAngle) {

        if (maxValue == 0 || maxValue - minValue == 0) {
            return;
        }

        float perValue = (maxValue - minValue) / measuresCount;
        int value = (int) (minValue + index * perValue);
        String measureValue = String.valueOf(value);

        textPaint.getTextBounds(measureValue, 0, measureValue.length(), mTextMeasures);

        float textAngle = startAngle + index * perAngle;
        PointF textPoint = getCoordinatePoint(mTextRadius, textAngle);
        float textStartX = 0, textStartY = 0;

        textStartX = textPoint.x - mTextMeasures.width() / 2;
        textStartY = textPoint.y + (mTextMeasures.height() / 2);

        canvas.drawText(measureValue, textStartX, textStartY, textPaint);

    }

    private void drawCenterCircle(Canvas canvas) {
        canvas.drawArc(centerCircleRecf, startAngle, sweepAngle, false, centerCirclePaint);
    }

    private void drawStripe(Canvas canvas) {

        stripePaint.setColor(Color.parseColor("#eeeeee"));
        canvas.drawArc(stripeRecf, startAngle, sweepAngle, false, stripePaint);

        stripePaint.setColor(Color.parseColor("#d2dfe8"));
        canvas.drawArc(stripeRecf, startAngle, currentValueAngle, false, stripePaint);

    }

    private void drawOuter(Canvas canvas) {

        float spacingAngle = sweepAngle / outerNums;

        for (int i = 0; i < outerNums; i++) {

            outerPaint.setColor(outerLineColors[i]);
            canvas.drawArc(outerRecf, startAngle + i * spacingAngle, spacingAngle, false, outerPaint);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = measureSize(widthMeasureSpec);
        int height = width / 2 + dpToPx(10);


        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize(w);
    }

    private void initSize(int width) {
        //中心点
        centerPoint = new PointF();
        centerPoint.set(width / 2.0f, width / 2.0f);

        //外圆半径
        mOuterRadius = (width - 2 * padding) / 2.0f - outerLineWidth / 2;

        outerRecf = new RectF();
        outerRecf.set(centerPoint.x - mOuterRadius, centerPoint.y - mOuterRadius,
                centerPoint.x + mOuterRadius, centerPoint.y + mOuterRadius);

        //彩条宽度
        stripeLineWidth = (mOuterRadius - outerLineWidth / 2 - dpToPx(25)) / 2;
        stripePaint.setStrokeWidth(stripeLineWidth);
        stripeRadius = mOuterRadius - outerLineWidth / 2 - stripeLineWidth / 2;
        mTextRadius = stripeRadius - stripeLineWidth / 4;
        stripeRecf = new RectF();
        stripeRecf.set(centerPoint.x - stripeRadius, centerPoint.y - stripeRadius,
                centerPoint.x + stripeRadius, centerPoint.y + stripeRadius);

        //内圆
        centerCircleRadius = stripeRadius - (stripeLineWidth + centerCircleWidth) / 2;
        centerCircleRecf = new RectF();
        centerCircleRecf.set(centerPoint.x - centerCircleRadius, centerPoint.y - centerCircleRadius,
                centerPoint.x + centerCircleRadius, centerPoint.y + centerCircleRadius);

        //指针半径
        pointerRadius = centerCircleRadius + dpToPx(10);

        initPointer();
    }


    private int measureSize(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //设置一个默认值，就是这个View的默认宽度为500，这个看我们自定义View的要求
        int result = dpToPx(110) * 2;
        if (specMode == MeasureSpec.AT_MOST) {//相当于我们设置为wrap_content
            result = specSize < result ? result : specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {//相当于我们设置为match_parent或者为一个具体的值
            result = specSize < result ? result : specSize;
        }
        return result;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int spToPx(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * 根据角度和半径计算出坐标
     */
    private PointF getCoordinatePoint(float radius, float angle) {

        PointF destPoint = new PointF();

        double radians = Math.toRadians(angle); //将角度转换为弧度

        destPoint.x = (float) (centerPoint.x + Math.cos(radians) * radius);
        destPoint.y = (float) (centerPoint.y + Math.sin(radians) * radius);

        return destPoint;
    }

    /**
     * 根据角度和半径计算出坐标
     */
    private PointF getCoordinatePoint(PointF srcPoint, float radius, float angle) {

        PointF destPoint = new PointF();

        double radians = Math.toRadians(angle); //将角度转换为弧度

        destPoint.x = (float) (srcPoint.x + Math.cos(radians) * radius);
        destPoint.y = (float) (srcPoint.y + Math.sin(radians) * radius);

        return destPoint;
    }

    /**
     * 设置最值
     */
    public void setValues(float minValue, float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        invalidate();
    }

    /**
     * 设置当前值
     *
     * @param currentValue
     */
    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;

        if (currentValue < minValue || currentValue > maxValue) {
            return;
        }

        float scale = (currentValue - minValue) / (maxValue - minValue);
        currentValueAngle = sweepAngle * scale;

//        initPointer();
        invalidate();
    }
}
