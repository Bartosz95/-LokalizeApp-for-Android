package com.example.home.projekt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import static android.support.v4.media.VolumeProviderCompat.VOLUME_CONTROL_RELATIVE;

public class ListeningLoop extends Service {

    private static ServiceCallbacks serviceCallbacks;
    private final IBinder binder = new ListeningLoop.LocalBinder();

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
        serviceCallbacks = callbacks;
    }

    /*@Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/


    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(this, "ListeningLoop");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING, 0, 0).build());
        VolumeProviderCompat myVolumeProvider = new VolumeProviderCompat(VOLUME_CONTROL_RELATIVE ,100,0) {
                    @Override
                    public void onAdjustVolume(int direction) {
                        if (serviceCallbacks != null) {
                            serviceCallbacks.startAlarm();
                        }
                    }
                };
        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);
    }
}