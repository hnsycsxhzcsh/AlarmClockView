package com.alarmclockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * Created by HARRY on 2019/1/4 0004.
 */

public class AlarmClockView extends View {

    private int mSecondHandColor;
    private int mMinuteHandColor;
    private int mHourHandColor;
    private int mMinuteScaleColor;
    private int mPointScaleColor;
    private int mDateValueColor;
    private int mClockWid;
    private int mOuterRadius;
    private int mCenterX;
    private int mCenterY;
    private int mWid;
    private int mHei;
    private Paint mPaint = new Paint();
    private int mOuterCircleColor;
    private int mInnerCircleColor;
    private int mInnerRadius;
    private int mSpace = 10;
    private int mHour;
    private int mMinute;
    private int mSecond;
    private int mScaleHei;
    private String[] arr = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private int mDay;
    private int mWeek;
    private int mMonth;
    private int mYear;
    private boolean mIsShowTime;
    private String mWeekStr;

    private Handler mHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            initCurrentTime();
        }
    };


    public AlarmClockView(Context context) {
        this(context, null);
    }

    public AlarmClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AlarmClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AlarmClockView);
        if (array != null) {
            mOuterCircleColor = array.getColor(R.styleable.AlarmClockView_outerCircleColor, getResources().getColor(R.color.gray));
            mInnerCircleColor = array.getColor(R.styleable.AlarmClockView_innerCircleColor, getResources().getColor(R.color.grayInner));
            mSecondHandColor = array.getColor(R.styleable.AlarmClockView_secondHandColor, getResources().getColor(R.color.green));
            mMinuteHandColor = array.getColor(R.styleable.AlarmClockView_minuteHandColor, getResources().getColor(R.color.black));
            mHourHandColor = array.getColor(R.styleable.AlarmClockView_hourHandColor, getResources().getColor(R.color.black));
            mMinuteScaleColor = array.getColor(R.styleable.AlarmClockView_minuteScaleColor, getResources().getColor(R.color.black));
            mPointScaleColor = array.getColor(R.styleable.AlarmClockView_scaleColor, getResources().getColor(R.color.gray));
            mDateValueColor = array.getColor(R.styleable.AlarmClockView_dateValueColor, getResources().getColor(R.color.black));
            mIsShowTime = array.getBoolean(R.styleable.AlarmClockView_isShowTime, true);

            array.recycle();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWid = w;
        mHei = h;
        mClockWid = w * 6 / 8;
        mOuterRadius = mClockWid / 2;
        mInnerRadius = mOuterRadius - mSpace;
        mCenterX = w / 2;
        mCenterY = mCenterX;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, mWid, mHei, mPaint);

        //画外层圆
        drawOuterCircle(canvas);

        //画内层圆
        drawInnerCircle(canvas);

        //画刻度
        canvas.save();
        drawTickMark(canvas);
        canvas.restore();

        //画刻度值
        drawScaleValue(canvas);

        //画针
        drawHand(canvas);

        //画现在时间显示
        if (mIsShowTime) {
            drawCurrentTime(canvas);
        }
    }

    private void drawCurrentTime(Canvas canvas) {
        mPaint.setColor(mDateValueColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(40);

        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();
        int baseLineY = mCenterY + mOuterRadius - fm.top + 2 * mSpace;

        String time = "" + mYear + "年" + (mMonth + 1) + "月" + mDay + "日，" + mWeekStr + "，" + mHour + "点" + mMinute + "分" + mSecond + "秒";
        canvas.drawText(time, mCenterX, baseLineY, mPaint);
    }

    private void drawHand(Canvas canvas) {
        //画时针
        canvas.save();
        int hourWid = 16;
        mPaint.setColor(mHourHandColor);
        mPaint.setStrokeWidth(hourWid);

        for (int i = 1; i <= 12; i++) {
            canvas.rotate(30, mCenterX, mCenterY);
            if (i == mHour) {
                //计算时针的偏移量
                int offset = (int) (((float) mMinute / (float) 60) * (float) 30);
                canvas.rotate(offset, mCenterX, mCenterY);
                RectF rectF = new RectF(mCenterX - hourWid / 2, mCenterY - mInnerRadius + mScaleHei + 3 * mSpace, mCenterX + hourWid / 2, mCenterY);
                canvas.drawRoundRect(rectF, hourWid / 2, hourWid / 2, mPaint);
                break;
            }
        }
        canvas.restore();

        //画分针
        canvas.save();
        int minuteWid = 10;
        mPaint.setColor(mMinuteHandColor);
        mPaint.setStrokeWidth(10);

        for (int i = 0; i < 60; i++) {
            if (i == mMinute) {
                //计算分针的偏移量
                int offset = (int) ((float) mSecond / (float) 60 * (float) 6);
                canvas.rotate(offset, mCenterX, mCenterY);
                RectF rectF = new RectF(mCenterX - minuteWid / 2, mCenterY - mInnerRadius + 3 * mSpace, mCenterX + minuteWid / 2, mCenterY);
                canvas.drawRoundRect(rectF, minuteWid / 2, minuteWid / 2, mPaint);
                break;
            } else {
                canvas.rotate(6, mCenterX, mCenterY);
            }
        }
        canvas.restore();

        //画秒针
        canvas.save();
        mPaint.setColor(mSecondHandColor);
        mPaint.setStrokeWidth(3);

        canvas.drawCircle(mCenterX, mCenterY, mSpace, mPaint);

        for (int i = 0; i < 60; i++) {
            if (i == mSecond) {
                canvas.drawLine(mCenterX, mCenterY + 3 * mSpace, mCenterX, mCenterY - mInnerRadius + mSpace, mPaint);
                break;
            } else {
                canvas.rotate(6, mCenterX, mCenterY);
            }
        }
        canvas.restore();

    }

    private void drawScaleValue(Canvas canvas) {
        mPaint.setColor(mPointScaleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(30);

        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();
        mScaleHei = fm.bottom - fm.top;

        for (int i = 0; i < 12; i++) {
            String degree = (i + 1) + "";
            float[] temp = calculatePoint((i + 1) * 30, mInnerRadius - mSpace * 4 - mPaint.getTextSize() / 2);
            canvas.drawText(degree, temp[2] + mCenterX, mCenterY + temp[3] + mPaint.getTextSize() / 2, mPaint);
        }
    }

    //计算线段的起始坐标
    private float[] calculatePoint(float angle, float length) {
        int POINT_BACK_LENGTH = 1;
        float[] points = new float[4];
        if (angle <= 90f) {
            points[0] = -(float) Math.sin(angle * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = (float) Math.cos(angle * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = (float) Math.sin(angle * Math.PI / 180) * length;
            points[3] = -(float) Math.cos(angle * Math.PI / 180) * length;
        } else if (angle <= 180f) {
            points[0] = -(float) Math.cos((angle - 90) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = -(float) Math.sin((angle - 90) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = (float) Math.cos((angle - 90) * Math.PI / 180) * length;
            points[3] = (float) Math.sin((angle - 90) * Math.PI / 180) * length;
        } else if (angle <= 270f) {
            points[0] = (float) Math.sin((angle - 180) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = -(float) Math.cos((angle - 180) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = -(float) Math.sin((angle - 180) * Math.PI / 180) * length;
            points[3] = (float) Math.cos((angle - 180) * Math.PI / 180) * length;
        } else if (angle <= 360f) {
            points[0] = (float) Math.cos((angle - 270) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = (float) Math.sin((angle - 270) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = -(float) Math.cos((angle - 270) * Math.PI / 180) * length;
            points[3] = -(float) Math.sin((angle - 270) * Math.PI / 180) * length;
        }
        return points;
    }

    private void drawTickMark(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                mPaint.setColor(mPointScaleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(5);
                mPaint.setAntiAlias(true);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setTextSize(30);

                canvas.drawLine(mCenterX, mSpace * 2 + mCenterY - mOuterRadius, mCenterX, mSpace * 4 + mCenterY - mOuterRadius, mPaint);
            } else {
                mPaint.setColor(mMinuteScaleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(2);
                mPaint.setAntiAlias(true);

                canvas.drawLine(mCenterX, mSpace * 2 + mCenterY - mOuterRadius, mCenterX, mSpace * 3 + mCenterY - mOuterRadius, mPaint);
            }

            canvas.rotate(6, mCenterX, mCenterY);
        }

    }

    private void drawInnerCircle(Canvas canvas) {
        mPaint.setColor(mInnerCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius - mSpace, mPaint);
    }

    private void drawOuterCircle(Canvas canvas) {
        mPaint.setColor(mOuterCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mPaint);
    }

    public void initCurrentTime() {
        Calendar mCalendar = Calendar.getInstance();
        //因为获取的时间总是晚一秒，这里加上这一秒
        mCalendar.add(Calendar.SECOND, 1);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        mHour = mCalendar.get(Calendar.HOUR);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mSecond = mCalendar.get(Calendar.SECOND);
        Calendar calendar = Calendar.getInstance();
        //1.数组下标从0开始；2.老外的第一天是从星期日开始的
        mWeekStr = arr[calendar.get(calendar.DAY_OF_WEEK) - 1];

        System.out.println("现在时间：小时：" + mHour + ",分钟：" + mMinute + ",秒：" + mSecond);

        invalidate();
    }

    public void start() {
        mHandler.postDelayed(runnable, 1000);
        initCurrentTime();
    }

    public void stop() {
        mHandler.removeCallbacks(runnable);
    }
}
