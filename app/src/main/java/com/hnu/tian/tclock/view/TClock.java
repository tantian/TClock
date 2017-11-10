package com.hnu.tian.tclock.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Date;

/**
 * Created by Tantian on 2017/11/10.
 */

public class TClock extends View {
    //画笔
    private Paint mPaint;

    //View的宽、高
    private int mWidth;
    private int mHeight;
    //边距
    private int padding = 20;

    //刻度宽度、高度
    private int numberWidth;
    private int numberHeight;

    //表盘宽度
    private int sufaceWidth;

    //时、分、秒
    private int mHour = 10;
    private int mMinute = 8;
    private int mSecond = 29;

    //
    private ValueAnimator mValueAnimator;


    public TClock(Context context) {
        super(context);
    }

    public TClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化参数
     */
    private void init(){
        mPaint = new Paint();
    }

    /**
     * 根据View宽度计算各部件宽高
     */
    private void caculatePartSize(){
        padding = mWidth / 10;

        sufaceWidth = mWidth / 500;

        numberWidth = mWidth / 300;
        numberHeight = mWidth / 25;

    }

    /**
     * 获取时分秒
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        mHour = calendar.get(Calendar.HOUR);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = mHeight = w;
        caculatePartSize();

        startRun();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getTime();
        drawClockShadow(canvas);
        drawClockSuface(canvas);
        drawClockNumber(canvas);
        drawClockPointer(canvas);
        startRun();
    }

    /**
     * 表盘阴影
     * @param canvas
     */
    private void drawClockShadow(Canvas canvas){
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setARGB(245,245,245,245);
        mPaint.setStyle(Paint.Style.FILL);
        RectF rectShadow = new RectF();
        rectShadow.set(padding * 3/2, mHeight - padding * 3 / 2, mWidth - padding * 3/2, mHeight - padding/3);
        canvas.drawOval(rectShadow, mPaint);
    }

    /**
     * 绘制表盘
     * @param canvas
     */
    private void drawClockSuface(Canvas canvas){

        int radius = mWidth / 2 - padding;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mWidth / 2, mHeight / 2, radius, mPaint);
//
//        mPaint.setARGB(	255,255,255,255);
//        RectF rectF = new RectF(padding, padding * 2, mWidth - padding, mHeight - padding*2);
//        canvas.drawOval(rectF, mPaint);

        mPaint.setStrokeWidth(sufaceWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(mWidth / 2, mHeight / 2, radius + sufaceWidth * 8, mPaint);

        mPaint.setStrokeWidth(sufaceWidth / 2);
        canvas.drawCircle(mWidth / 2, mHeight / 2, radius + sufaceWidth * 6, mPaint);

        mPaint.setStrokeWidth(sufaceWidth * 8);
        canvas.drawCircle(mWidth / 2, mHeight / 2, radius, mPaint);

        mPaint.setStrokeWidth(sufaceWidth);
        canvas.drawCircle(mWidth / 2, mHeight / 2, radius - sufaceWidth * 8, mPaint);

        canvas.drawCircle(mWidth / 2, mHeight / 2, radius - sufaceWidth * 11, mPaint);

        canvas.drawCircle(mWidth / 2, mHeight / 2, radius - sufaceWidth * 16, mPaint);

    }

    /**
     * 绘制表盘刻度
     * @param canvas
     */
    private void drawClockNumber(Canvas canvas){
        int radius = mWidth / 4;
        int tmp = mWidth / 2;
        int bottom = tmp - radius;
        int top = bottom - numberHeight;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        for(int i = 0; i < 60; i++){
            //变量K来控制刻度的粗细程度
            int k = 1;
            if(i % 5 == 0){
                k = 2;
            }
            canvas.save();
            canvas.rotate(i * 6, mWidth/2, mHeight/2);
            canvas.drawRect(tmp - numberWidth / 2 * k, top, tmp + numberWidth / 2 * k, bottom, mPaint);
            canvas.restore();
        }
    }

    /**
     * 绘制指针
     * @param canvas
     */
    private void drawClockPointer(Canvas canvas){
        //先画出分针
        //中间圆环
        mPaint.setStrokeWidth(numberWidth * 2);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mWidth/2, mHeight/2, numberHeight/2, mPaint);
        //分钟指针
        canvas.save();
        canvas.rotate(mMinute * 6 + mSecond / 10, mWidth/2, mHeight/2);
        mPaint.setStrokeWidth(numberWidth * 3);
        mPaint.setStyle(Paint.Style.FILL);
        int x = mWidth/2;
        int y = mHeight/2 - numberHeight/2;
        canvas.drawLine(x, y, x, y - mWidth/4 - numberHeight * 8/9, mPaint);
        canvas.restore();

        //再画时针
        canvas.save();
        canvas.rotate(mHour * 30 + mMinute / 2, mWidth/2, mHeight/2);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(numberHeight - numberWidth * 2);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        int x2 = mWidth/2;
        int y2 = mHeight/2;
        canvas.drawLine(x2, y2, x2, y2 - mWidth/5, mPaint);
        canvas.restore();

        //最后画秒针
        canvas.save();
        canvas.rotate(mSecond * 6, mWidth/2, mHeight/2);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(numberWidth * 2);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        int x3 = mWidth/2;
        int y3 = mHeight/2;
        canvas.drawLine(x3, y3 + mWidth/12, x3, y3 - mWidth/5, mPaint);
        canvas.restore();
        //秒针上两个圆
        canvas.drawCircle(mWidth/2, mHeight/2, numberHeight/4, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mWidth/2, mHeight/2, numberHeight/20, mPaint);

    }

    public void startRun(){
        postInvalidateDelayed(500);
    }

    private void startInvalidateAnim() {
        if (mValueAnimator==null) {
            mValueAnimator = ValueAnimator.ofInt(0, 60);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.setDuration(60 * 1000);
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            mValueAnimator.setRepeatMode(ValueAnimator.);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mSecond = (int) animation.getAnimatedValue();

                    invalidate();
                }
            });
            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mMinute += 1;
                    if (mMinute == 60) {
                        mHour = (mHour + 1) % 12;
                    }
                    mMinute %= 60;

                }
            });
        }
        mValueAnimator.start();
    }

}
