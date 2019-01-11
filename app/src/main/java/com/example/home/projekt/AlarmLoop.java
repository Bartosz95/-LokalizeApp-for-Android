package com.example.home.projekt;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AlarmLoop extends Service {

    private static ServiceCallbacks mainActivity;
    private final IBinder binder = new LocalBinder();
    private Timer timer;
    private TimerTask timerTask;


    public class LocalBinder extends Binder {
        AlarmLoop getService() {
            return AlarmLoop.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public static void setCallbacks(ServiceCallbacks callbacks) {
        mainActivity = callbacks;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() { // activate MainActivity.sendMessage()
            if (mainActivity != null) {
                mainActivity.sendMessage();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clearTimerSchedule();
        initTask();
        timer.scheduleAtFixedRate(timerTask, 0, 3*1000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void clearTimerSchedule() {
        if(timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    private void initTask() {
        timerTask = new AlarmLoop.MyTimerTask();
    }

    @Override
    public void onDestroy() {
        clearTimerSchedule();
        super.onDestroy();
    }
}
