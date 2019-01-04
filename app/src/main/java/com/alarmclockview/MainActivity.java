package com.alarmclockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private AlarmClockView mClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClock = findViewById(R.id.clock);

        mClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClock.start();
            }
        });
    }
}
