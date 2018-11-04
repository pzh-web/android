package com.example.test.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.service.MusicService;

import java.util.List;

public class MusicActivity extends Activity implements AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {
    private Handler hd = new Handler();
    private MusicService.MusicBinder binder;
    SeekBar bar;
    TextView curTime;
    TextView totalTime;
    ListView lv;
    String musicName = null;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            binder = (MusicService.MusicBinder) service;
//            binder.setLv(lv);
            // 音乐列表
            List<String> musicList = binder.getMusicList();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MusicActivity.this, android.R.layout.simple_list_item_1, musicList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(MusicActivity.this);
            // 监听歌曲播放进度
            hd.post(mRunnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        bar = findViewById(R.id.musicBar);
        curTime = findViewById(R.id.curTime);
        totalTime = findViewById(R.id.totalTime);
        lv = findViewById(R.id.musicList);


        // 1、开启权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        //2、启动service
        Intent intent = new Intent();
        intent.setClass(this, MusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        // bar改变事件
        bar.setOnSeekBarChangeListener(MusicActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 退出时不销毁当前activity
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void pre(View view) {
        binder.setPause(false);
        binder.pre(bar, totalTime);
    }

    public void play(View view) {
        binder.play(musicName, bar, totalTime);
    }

    public void next(View view) {
        binder.setPause(false);
        binder.next(bar, totalTime);
    }

    public void pause(View view) {
        binder.setPause(true);
        binder.pause();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (binder.getPlayPosition() != 0 && curTime.getText().equals(totalTime.getText()))
                binder.next(bar, totalTime);
            bar.setProgress(binder.getPlayPosition());
            binder.setLv(lv);
            if (binder.getPlayPosition() != 0)
                curTime.setText(binder.getPlayPosition() / 1000 / 60 + ":" + binder.getPlayPosition() / 1000 % 60);
            hd.postDelayed(mRunnable, 50);
        }
    };

    // 为ListView绑定事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        musicName = (String) parent.getItemAtPosition(position);
        binder.setPause(false);
        binder.play(musicName, bar, totalTime);
    }

    // 为seekbar绑定事件
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            binder.seekToPositon(seekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
