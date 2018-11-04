package com.example.test.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    MusicBinder binder;
    MediaPlayer player;
    List<String> musicList;
    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";

    public MusicService() {
        player = new MediaPlayer();
        player.setLooping(true);
        binder = new MusicBinder();
        musicList = new ArrayList<String>();
        File mp3Dir = new File(path);
        if (mp3Dir.isDirectory()) {
            File[] fs = mp3Dir.listFiles();
            for (File f : fs) {
                String musicName = f.getName();
                musicList.add(musicName.substring(musicName.lastIndexOf("/") + 1));
            }
        }
        binder.setMusicList(musicList);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MusicBinder extends Binder {
        private List<String> musicList;
        private ListView lv;

        public ListView getLv() {
            return lv;
        }

        public void setLv(ListView lv) {
            this.lv = lv;
        }

        private String curMusicName;
        private boolean isPause = false;
        Integer curMusicIndex;

        public Integer getCurMusicIndex() {
            return curMusicIndex;
        }

        public void setCurMusicIndex(Integer curMusicIndex) {
            this.curMusicIndex = curMusicIndex;
        }

        public boolean isPause() {
            return isPause;
        }

        public void setPause(boolean pause) {
            isPause = pause;
        }

        public String getCurMusicName() {
            return curMusicName;
        }

        public void setCurMusicName(String curMusicName) {
            this.curMusicName = curMusicName;
        }

        public List<String> getMusicList() {
            return musicList;
        }

        public void setMusicList(List<String> musicList) {
            this.musicList = musicList;
        }

        /**
         * 播放
         */
        public void play(String musicName, SeekBar bar, TextView totalTime) {
            changeLvBackgroud(this.musicList.indexOf(musicName));
            if (musicName != null && musicName != "")
                if (!isPause) {
                    this.curMusicName = musicName;
                    this.curMusicIndex = this.musicList.indexOf(musicName);
                    try {
                        player.reset();
                        player.setDataSource(path + musicName);
                        player.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            player.start();
            // 设置seekbar总长度
            int duration = player.getDuration();
            bar.setMax(duration);
            // 设置总时长
            totalTime.setText(duration / 1000 / 60 + ":" + duration / 1000 % 60);
        }

        /**
         * 暂停
         */
        public void pause() {
            if (player.isPlaying())
                player.pause();
        }

        /**
         * 上一首
         */
        public void pre(SeekBar bar, TextView totalTime) {
            this.curMusicIndex = this.musicList.indexOf(this.curMusicName);
            int preMusicIndex = musicList.size() - 1;
            if (curMusicIndex > 0)
                preMusicIndex = this.curMusicIndex - 1;
            changeLvBackgroud(preMusicIndex);
            play(musicList.get(preMusicIndex), bar, totalTime);
        }

        /**
         * 下一首
         */
        public void next(SeekBar bar, TextView totalTime) {
            this.curMusicIndex = this.musicList.indexOf(this.curMusicName);
            int nextMusicIndex = 0;
            if (curMusicIndex < musicList.size() - 1)
                nextMusicIndex = this.curMusicIndex + 1;
            changeLvBackgroud(nextMusicIndex);
            play(musicList.get(nextMusicIndex), bar, totalTime);
        }

        /**
         * 获取歌曲长度
         **/
        public int getProgress() {
            if (player.isPlaying() || isPause)
                return player.getDuration();
            else
                return 0;
        }

        /**
         * 获取播放位置
         */
        public int getPlayPosition() {
            if (player.isPlaying() || isPause)
                return player.getCurrentPosition();
            else
                return 0;
        }

        /**
         * 播放指定位置
         */
        public void seekToPositon(int msec) {
            player.seekTo(msec);
        }

        /**
         * 更改ListView的颜色
         */
        private void changeLvBackgroud(int position) {
            View v = null;
            if (this.curMusicIndex!=null){
                v = lv.getChildAt(this.curMusicIndex);
                v.setBackgroundColor(Color.WHITE);
            }
            v = lv.getChildAt(position);
            v.setBackgroundColor(Color.RED);
        }
    }
}
