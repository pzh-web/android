package com.example.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.test.R;

/**
 * 1、主线程不能运行长作业
 * 2、子线程不能更改主线程
 */
public class Tread1Activity extends Activity {
    private TextView tv;
    private Handler hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread1_activity);
        tv = findViewById(R.id.tv);
        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str = msg.getData().getString("random num");
                tv.setText(str);
            }
        };
    }

    public void start(View view) {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.getData().putString("random num", String.valueOf(Math.random()));
                    hd.sendMessage(msg);
                }
            }
        }.start();
    }
}