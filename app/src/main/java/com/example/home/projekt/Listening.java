package com.example.home.projekt;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import static android.support.v4.media.VolumeProviderCompat.VOLUME_CONTROL_RELATIVE;

public class ListeningLoop extends Service {

    private static ServiceCallbacks mainActivity;
    private final IBinder binder = new LocalBinder();

    int counter = 0;
    Toast toast;

    private MediaSessionCompat mediaSession;

    public class LocalBinder extends Binder {
        ListeningLoop getService() {
            return ListeningLoop.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public static void setCallbacks(ServiceCallbacks callbacks) {
        mainActivity = callbacks;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        counter=0;
        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        mediaSession = new MediaSessionCompat(this, "ListeningLoop");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING, 0, 0).build());
        VolumeProviderCompat myVolumeProvider = new VolumeProviderCompat(VOLUME_CONTROL_RELATIVE ,100,0) {
            @Override
            public void onAdjustVolume(int direction) {
                super.onAdjustVolume(direction);
                counter++;
                if (counter == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                if ((counter > 80) && (mainActivity != null)){
                                    counter = 0;
                                    mainActivity.startAlarm();
                                }
                                counter = 0;
                            }
                        }
                    }).start();
                }
            }
        };
        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);
    }
}