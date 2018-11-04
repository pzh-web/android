package com.example.test.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.entity.MyBinder;
import com.example.test.service.MyService;

public class MainActivity extends Activity {

    private int[] color = {250, 0, 0};
    private SeekBar sb;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final MyBinder binder = (MyBinder) service;
            new Thread("child1") {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sb.setProgress(binder.getProgress());
                    }
                }
            }.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar barR = findViewById(R.id.bar_r);
        SeekBar barG = findViewById(R.id.bar_g);
        SeekBar barB = findViewById(R.id.bar_b);
        bindMoveEventForBar(barR);
        bindMoveEventForBar(barG);
        bindMoveEventForBar(barB);

        sb = findViewById(R.id.sb);
    }

    private void setBackgroundByColor() {
        TextView txt = findViewById(R.id.txt);
        int c = Color.rgb(color[0], color[1], color[2]);
        txt.setBackgroundColor(c);
    }

    private void bindMoveEventForBar(SeekBar bar) {
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int id = seekBar.getId();
                if (id == R.id.bar_r)
                    color[0] = progress;
                else if (id == R.id.bar_g)
                    color[1] = progress;
                else
                    color[2] = progress;
                setBackgroundByColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // 点击跳转页面
    public void startOther(View view) {
        // 弹框
//        Toast.makeText(this,"button",Toast.LENGTH_LONG).show();
        Log.i("test", "log");
        Intent intent = new Intent();
        // 传参数
        intent.putExtra("uname", "zhangsan");
        // 显示打开
        intent.setClass(this, OtherActivity.class);
        // 跳转
        startActivity(intent);
    }

    public void testService(View view) throws Exception {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("name", "zhangsan");
        // 启动service
        startService(intent);
//        Thread.sleep(2000);
        //停止service
        stopService(intent);
    }

    public void bindService(View view) throws Exception {
        Intent intent = new Intent();
            intent.setClass(this, MyService.class);
            bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public void stop(View view) throws Exception{
    }
}
