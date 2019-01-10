package com.example.home.projekt;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class AlarmLoop extends Service {
    private static ServiceCallbacks serviceCallbacks;
    private final IBinder binder = new LocalBinder();
    private Toast toast;
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
        serviceCallbacks = callbacks;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() { // activate MainActivity.sendMessage()
            if (serviceCallbacks != null) {
                serviceCallbacks.sendMessage();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clearTimerSchedule();
        initTask();
        timer.scheduleAtFixedRate(timerTask, 0, 3*60*1000);
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
