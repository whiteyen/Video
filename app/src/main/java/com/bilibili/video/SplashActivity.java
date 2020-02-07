package com.bilibili.video;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.main)
    TextView main;
    private SharedPreferences mSharedPreferences;
    private static final int GO_HOME = 1;
    private static final int GO_GUIDE = 2;

    private static int ENTER_DURATION = 3000;//3s
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    startHome();
                    break;
                case GO_GUIDE:
                    startGuide();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        init();
    }

    private void init() {
        boolean isFirstIn = mSharedPreferences.getBoolean("mIsFirstIn", true);
        if (isFirstIn) {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, ENTER_DURATION);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_HOME, ENTER_DURATION);
        }
    }

    private void startHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }
}
