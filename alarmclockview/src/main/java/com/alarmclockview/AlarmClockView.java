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

    /**
     * 秒针颜色
     */
    private int mSecondHandColor;
    /**
     * 分针颜色
     */
    private int mMinuteHandColor;
    /**
     * 时针颜色
     */
    private int mHourHandColor;
    /**
     * 分钟刻度颜色
     */
    private int mMinuteScaleColor;
    /**
     * 当分钟是5的倍数时刻度的颜色
     */
    private int mPointScaleColor;
    /**
     * 时钟底部时间文本颜色
     */
    private int mDateValueColor;
    /**
     * 时钟宽度
     */
    private int mClockWid;
    /**
     * 时钟最外层圆半径
     */
    private int mOuterRadius;
    /**
     * 时钟圆心x
     */
    private int mCenterX;
    /**
     * 时钟圆心y
     */
    private int mCenterY;
    /**
     * 控件宽
     */
    private int mWid;
    /**
     * 控件高
     */
    private int mHei;
    private Paint mPaint = new Paint();
    /**
     * 最外层圆颜色
     */
    private int mOuterCircleColor;
    /**
     * 内层圆颜色
     */
    private int mInnerCircleColor;
    /**
     * 内层半径
     */
    private int mInnerRadius;
    /**
     * 内外圆的间距
     */
    private int mSpace = 10;
    /**
     * 现在的时间小时
     */
    private int mHour;
    /**
     * 现在的时间分钟
     */
    private int mMinute;
    /**
     * 现在的时间秒
     */
    private int mSecond;
    /**
     * 时钟上刻度值的高度
     */
    private int mScaleValueHei;
    private String[] arr = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    /**
     * 现在的时间天
     */
    private int mDay;
    /**
     * 现在的时间周几
     */
    private int mWeek;
    /**
     * 现在的时间月
     */
    private int mMonth;
    /**
     * 现在的时间年
     */
    private int mYear;
    /**
     * 是否显示时钟底部的时间文本
     */
    private boolean mIsShowTime;
    /**
     * 真实的周几
     */
    private String mWeekStr;
    /**
     * 时间监听
     */
    private TimeChangeListener listener;
    /**
     * 时钟占空间整体的比例
     */
    private float mProportion;
    /**
     * 是否为夜间模式
     */
    private boolean mIsNight;
    private Context context;
    private AttributeSet attrs;

    /**
     * handler用来处理定时任务，没隔一秒刷新一次
     */
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            initCurrentTime();
        }
    };
    private int mApm;

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
        this.context = context;
        this.attrs = attrs;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AlarmClockView);
        if (array != null) {
            mOuterCircleColor = array.getColor(R.styleable.AlarmClockView_outerCircleColor, getResources().getColor(R.color.gray));
            mInnerCircleColor = array.getColor(R.styleable.AlarmClockView_innerCircleColor, getResources().getColor(R.color.grayInner));
            mSecondHandColor = array.getColor(R.styleable.AlarmClockView_secondHandColor, getResources().getColor(R.color.green));
            mMinuteHandColor = array.getColor(R.styleable.AlarmClockView_minuteHandColor, getResources().getColor(R.color.black));
            mHourHandColor = array.getColor(R.styleable.AlarmClockView_hourHandColor, getResources().getColor(R.color.black));
            mMinuteScaleColor = array.getColor(R.styleable.AlarmClockView_minuteScaleColor, getResources().getColor(R.color.black));
            mPointScaleColor = array.getColor(R.styleable.AlarmClockView_scaleColor, getResources().getColor(R.color.black));
            mDateValueColor = array.getColor(R.styleable.AlarmClockView_dateValueColor, getResources().getColor(R.color.black));
            mIsShowTime = array.getBoolean(R.styleable.AlarmClockView_isShowTime, true);
            mProportion = array.getFloat(R.styleable.AlarmClockView_proportion, (float) 0.75);
            mIsNight = array.getBoolean(R.styleable.AlarmClockView_night, false);
            if (mProportion > 1 || mProportion < 0) {
                mProportion = (float) 0.75;
            }

            if (mIsNight) {
                setNightColor();
            }
            array.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWid = w;
        mHei = h;
        //使闹钟的宽为控件宽的mProportion;
        mClockWid = (int) (w * mProportion);
        mOuterRadius = mClockWid / 2;
        mInnerRadius = mOuterRadius - mSpace;
        mCenterX = w / 2;
        mCenterY = mCenterX;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int modeHei = MeasureSpec.getMode(heightMeasureSpec);
        if (modeHei == MeasureSpec.UNSPECIFIED || modeHei == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, width);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置整体控件的背景为白色背景
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, mWid, mHei, mPaint);

        //画外层圆
        drawOuterCircle(canvas);

        //画内层圆
        drawInnerCircle(canvas);

        //画刻度
        drawTickMark(canvas);

        //画刻度值
        drawScaleValue(canvas);

        //画针
        drawHand(canvas);

        //画现在时间显示
        if (mIsShowTime) {
            drawCurrentTime(canvas);
        }
    }

    /**
     * 画时钟底部的时间文本
     *
     * @param canvas
     */
    private void drawCurrentTime(Canvas canvas) {
        mPaint.setColor(mDateValueColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(40);

        //使当前时间文本正好在时钟底部距离有2 * mSpace的位置
        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();
        int baseLineY = mCenterY + mOuterRadius - fm.top + 2 * mSpace;

        String apm = "";
        if (mApm == 0) {
            apm = "上午";
        } else {
            apm = "下午";
        }

        String time = "" + mYear + "年" + (mMonth + 1) + "月" + mDay + "日" + mWeekStr + apm + mHour + "点" + mMinute + "分" + mSecond + "秒";
        canvas.drawText(time, mCenterX, baseLineY, mPaint);
    }

    /**
     * 画时钟内的针
     *
     * @param canvas
     */
    private void drawHand(Canvas canvas) {
        //画时针
        canvas.save();
        int hourWid = 16;
        mPaint.setColor(mHourHandColor);
        mPaint.setStrokeWidth(hourWid);

        for (int i = 0; i < 12; i++) {
            if (i == mHour) {
                //计算时针的偏移量
                int offset = (int) (((float) mMinute / (float) 60) * (float) 30);
                canvas.rotate(offset, mCenterX, mCenterY);
                RectF rectF = new RectF(mCenterX - hourWid / 2, mCenterY - mInnerRadius + mScaleValueHei + 3 * mSpace, mCenterX + hourWid / 2, mCenterY);
                canvas.drawRoundRect(rectF, hourWid / 2, hourWid / 2, mPaint);
                break;
            } else {
                canvas.rotate(30, mCenterX, mCenterY);
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

    /**
     * 画时钟内的刻度值
     *
     * @param canvas
     */
    private void drawScaleValue(Canvas canvas) {
        mPaint.setColor(mPointScaleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(30);

        //计算刻度值的文本高度
        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();
        mScaleValueHei = fm.bottom - fm.top;

        for (int i = 0; i < 12; i++) {
            String degree = (i + 1) + "";
            float[] temp = calculatePoint((i + 1) * 30, mInnerRadius - mSpace * 4 - mPaint.getTextSize() / 2);
            canvas.drawText(degree, temp[2] + mCenterX, mCenterY + temp[3] + mPaint.getTextSize() / 2, mPaint);
        }
    }

    /**
     * 计算线段的起始坐标
     *
     * @param angle
     * @param length
     * @return
     */
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

    /**
     * 画时钟的刻度线
     *
     * @param canvas
     */
    private void drawTickMark(Canvas canvas) {
        canvas.save();

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

        canvas.restore();
    }

    /**
     * 画内圆
     *
     * @param canvas
     */
    private void drawInnerCircle(Canvas canvas) {
        mPaint.setColor(mInnerCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint);

        if (mIsNight) {
            mPaint.setColor(Color.BLACK);
        } else {
            mPaint.setColor(Color.WHITE);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius - mSpace, mPaint);
    }

    /**
     * 画外圆
     *
     * @param canvas
     */
    private void drawOuterCircle(Canvas canvas) {
        mPaint.setColor(mOuterCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mPaint);
    }

    /**
     * 获取当前时间
     */
    public void initCurrentTime() {
        Calendar mCalendar = Calendar.getInstance();
        resetTime(mCalendar);
        invalidate();
    }

    /**
     * 重置时间信息
     *
     * @param calendar
     */
    private void resetTime(Calendar calendar) {
        //因为获取的时间总是晚一秒，这里加上这一秒
        calendar.add(Calendar.SECOND, 1);
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mWeek = calendar.get(Calendar.DAY_OF_WEEK);
        mHour = calendar.get(Calendar.HOUR);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);
//        apm=0 表示上午，apm=1表示下午。
        mApm = calendar.get(Calendar.AM_PM);
        //1.数组下标从0开始；2.老外的第一天是从星期日开始的
        mWeekStr = arr[calendar.get(calendar.DAY_OF_WEEK) - 1];

        System.out.println("现在时间：小时：" + mHour + ",分钟：" + mMinute + ",秒：" + mSecond);

        if (listener != null) {
            listener.onTimeChange(calendar);
        }
    }

    /**
     * 运行闹钟
     *
     * @param listener
     */
    public void start(TimeChangeListener listener) {
        this.listener = listener;
        mHandler.postDelayed(runnable, 1000);
        initCurrentTime();
    }

    /**
     * 运行闹钟
     */
    public void start() {
        mHandler.postDelayed(runnable, 1000);
        initCurrentTime();
    }

    /**
     * 停止闹钟
     */
    public void stop() {
        mHandler.removeCallbacks(runnable);
    }

    /**
     * 设置是否为夜间模式
     *
     * @param isNight
     */
    public void setIsNight(boolean isNight) {
        mIsNight = isNight;

        judgeIsNight();
    }

    /**
     * 判断isNight属性下颜色值如何选择
     */
    private void judgeIsNight() {
        if (mIsNight) {
            setNightColor();
        } else {
            //这里没有night属性
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AlarmClockView);
            if (array != null) {
                mOuterCircleColor = array.getColor(R.styleable.AlarmClockView_outerCircleColor, getResources().getColor(R.color.gray));
                mInnerCircleColor = array.getColor(R.styleable.AlarmClockView_innerCircleColor, getResources().getColor(R.color.grayInner));
                mSecondHandColor = array.getColor(R.styleable.AlarmClockView_secondHandColor, getResources().getColor(R.color.green));
                mMinuteHandColor = array.getColor(R.styleable.AlarmClockView_minuteHandColor, getResources().getColor(R.color.black));
                mHourHandColor = array.getColor(R.styleable.AlarmClockView_hourHandColor, getResources().getColor(R.color.black));
                mMinuteScaleColor = array.getColor(R.styleable.AlarmClockView_minuteScaleColor, getResources().getColor(R.color.black));
                mPointScaleColor = array.getColor(R.styleable.AlarmClockView_scaleColor, getResources().getColor(R.color.black));
                mDateValueColor = array.getColor(R.styleable.AlarmClockView_dateValueColor, getResources().getColor(R.color.black));
                mIsShowTime = array.getBoolean(R.styleable.AlarmClockView_isShowTime, true);
                mProportion = array.getFloat(R.styleable.AlarmClockView_proportion, (float) 0.75);
                if (mProportion > 1 || mProportion < 0) {
                    mProportion = (float) 0.75;
                }

                array.recycle();
            }
        }
    }

    /**
     * 设置夜晚时的颜色
     */
    private void setNightColor() {
        mMinuteHandColor = Color.WHITE;
        mHourHandColor = Color.WHITE;

        mMinuteScaleColor = Color.WHITE;
        mPointScaleColor = Color.WHITE;

        mInnerCircleColor = Color.BLACK;
    }

    public boolean getIsNight() {
        return mIsNight;
    }

    /**
     * 自定义时间
     *
     * @param calendar
     */
    public void setCurrentTime(Calendar calendar) {
        stop();
        resetTime(calendar);
        invalidate();
    }

}
