package com.example.test.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.test.entity.MyBinder;

public class MyService extends Service {

    /**
     * Service中的onBind()方法是抽象方法，Service类本身就是抽象类，所以onBind()方法是必须重写的，即使我们用不到。
     */
    @Override
    public IBinder onBind(final Intent intent) {
        final MyBinder binder=new MyBinder();

        new Thread("child2"){
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int p=binder.getProgress();
                    binder.setProgress(++p);
                }
            }
        }.start();

        return binder;
    }

    /**
     * 1.如果service没被创建过，调用startService()后会执行onCreate()回调；
     * 2.如果service已处于运行中，调用startService()不会执行onCreate()方法。
     * 也就是说，onCreate()只会在第一次创建service时候调用，多次执行startService()不会重复调用onCreate()，此方法适合完成一些初始化工作。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service", "create....");
    }

    /**
     * 如果多次执行了Context的startService()方法，那么Service的onStartCommand()方法也会相应的多次调用。
     * onStartCommand()方法很重要，我们在该方法中根据传入的Intent参数进行实际的操作，比如会在此处创建一个线程用于下载数据或播放音乐等。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name = intent.getStringExtra("name");
        Log.i("name",name);
        Log.i("service","test service");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 在销毁的时候会执行Service该方法。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("service", "destroy....");
    }
}
