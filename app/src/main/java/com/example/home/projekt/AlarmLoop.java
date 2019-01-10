package com.example.home.projekt;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AlarmLoop extends Service {
    private Toast toast;
    private Timer timer;
    private TimerTask timerTask;

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            //Sms.SendMessage("mess","1234");
            showToast("AlarmLoop");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { ;
        clearTimerSchedule();
        initTask();
        timer.scheduleAtFixedRate(timerTask, 4 * 1000, 4*1000);
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
        showToast("Alarm stopped");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void showToast(String text) {
        toast.setText(text);
        toast.show();
    }

}
