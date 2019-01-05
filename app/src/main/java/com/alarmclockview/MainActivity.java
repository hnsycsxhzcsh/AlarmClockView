package com.alarmclockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlarmClockView mClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClock = findViewById(R.id.clock);
        //运行闹钟
        mClock.start(new TimeChangeListener() {
            @Override
            public void onTimeChange(Calendar calendar) {
                //根据calendar获取当前时间
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                if (hour >= 10 && hour <= 12) {
//                    mClock.setIsNight(true);
//                }
            }
        });

        final int[] offet = {1};
        mClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, offet[0]++);
                mClock.setCurrentTime(calendar);
            }
        });
    }
}
