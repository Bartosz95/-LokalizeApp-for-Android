package com.example.home.projekt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.VolumeProvider;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import static android.support.v4.media.VolumeProviderCompat.VOLUME_CONTROL_RELATIVE;

public class Listening extends Service {

    private static ServiceCallbacks mainActivity;
    private final IBinder binder = new LocalBinder();

    int counter = 0;
    Toast toast;

    private AudioManager audio;
    private MediaSessionCompat mediaSession;

    public class LocalBinder extends Binder {
        Listening getService() {
            return Listening.this;
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

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mediaSession = new MediaSessionCompat(this, "Listening");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                .build());
        VolumeProviderCompat myVolumeProvider = new VolumeProviderCompat(VOLUME_CONTROL_RELATIVE ,audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),audio.getStreamVolume(AudioManager.STREAM_MUSIC)) {
            @Override
            public void onAdjustVolume(int direction) {
                super.onAdjustVolume(direction);
                counter++;
                this.setCurrentVolume(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                    switch (direction) {
                        case 1:
                            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                            break;
                        case -1:
                            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                            break;
                    }

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