package com.example.home.projekt;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;

public class MainActivity extends AppCompatActivity implements ServiceCallbacks {

    private Toast toast;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private Location location;
    private Database db;
    private ArrayList<ArrayList<String>> listContacts;

    private Button btnEditSendMessage, btnEditContacts, btnSendMessage, btnAlarm, btnListening;

    public boolean isListening = false, isAlarm = false;
    private AlarmLoop alarmLoop;
    private Listening listening;
    private Intent intentAlarm, intentListening;
    private boolean boundAlarm = false, boundListening = false;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        if(!checkPermission()) {
            requestPermission();
        }

        location = new Location(this);
        db = new Database(this);
        handler = new Handler(getApplicationContext().getMainLooper());

        btnEditSendMessage = findViewById(R.id.btnEditSendMessage);
        btnEditSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Statement.class));
            }
        });

        btnEditContacts = findViewById(R.id.btnEditContacts);
        btnEditContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Contacts.class));
            }
        });

        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendMessage();
            }
        });

        btnListening = findViewById(R.id.btnListening);
        btnListening.setText("Start listening");
        btnListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopListening();
            }
        });

        btnAlarm = findViewById(R.id.btnAlarm);
        btnAlarm.setText("Start Alarm");
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListening) startStopListening();
                startStopAlarm();
            }
        });
    }

    private void toastMessage(String text) {
        toast.setText(text);
        toast.show();
    }

    // fcn activated/deactivated alarm
    private void startStopAlarm(){
        if(isAlarm){
            stopService(intentAlarm);
            btnAlarm.setText("Start Alarm");
            toastMessage("Alarm stopped");
            stop();
            isAlarm = false;
        } else { // wystartowanie alarmu
            sendMessage();
            intentAlarm = new Intent(MainActivity.this, AlarmLoop.class);
            bindService(intentAlarm, alarmServiceConnection, Context.BIND_AUTO_CREATE);
            startService(intentAlarm);
            btnAlarm.setText("Stop Alarm");
            toastMessage("Alarm started");
            isAlarm = true;
        }
        deactivatedButtons();
    }

    // fcn activated/deactivated listening
    private void startStopListening(){
        if(isListening){ // wylaczenie nasluchiwania
            stopService(intentListening);
            btnListening.setText("Start listening");
            toastMessage("Listening stopped");
            stop();
            isListening = false;
        } else {
            intentListening = new Intent(MainActivity.this, Listening.class);
            bindService(intentListening, listeningServiceConnection, Context.BIND_AUTO_CREATE);
            startService(intentListening);
            btnListening.setText("Stop listening");
            toastMessage("Listening started");
            isListening = true;
        }
        deactivatedButtons();
    }

    private void deactivatedButtons(){
        if(isAlarm || isListening){
            btnEditContacts.setEnabled(false);
            btnEditSendMessage.setEnabled(false);
        } else {
            btnEditContacts.setEnabled(true);
            btnEditSendMessage.setEnabled(true);
        }
        if(isAlarm){
            btnListening.setEnabled(false);
        } else {
            btnListening.setEnabled(true);
        }

    }

    public void startAlarm(){
        toastMessage("Alarm");
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isListening) startStopListening();
                        startStopAlarm();
                    }
                });
            }
        }).start();
    }

    @Override
    public void sendMessage() {
        listContacts = db.getPhoneNumbersList();
        StringBuilder message = new StringBuilder("You send message to:");
        for(int i=0;i<listContacts.size();i++){
            message.append("\n").append(listContacts.get(i).get(1));
            Sms.SendMessage(String.format("%s My localization is: %s",db.getStatement(),location.getLastLocationString()), listContacts.get(i).get(2) );
        }
        toastMessage(message.toString());
    }

    protected void stop() {
        if (boundAlarm) {
            AlarmLoop.setCallbacks(null);
            unbindService(alarmServiceConnection);
            boundAlarm = false;
        }
        if (boundListening) {
            Listening.setCallbacks(null);
            unbindService(listeningServiceConnection);
            boundListening = false;
        }
    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection alarmServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AlarmLoop.LocalBinder binder = (AlarmLoop.LocalBinder) service;
            alarmLoop = binder.getService();
            boundAlarm = true;
            AlarmLoop.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundAlarm = false;
        }
    };

    private ServiceConnection listeningServiceConnection= new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Listening.LocalBinder binder1 = (Listening.LocalBinder) service;
            listening = binder1.getService();
            boundListening = true;
            Listening.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundListening = false;
        }
    };

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_BOOT_COMPLETED);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, SEND_SMS, READ_CONTACTS, RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean location1Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean location2Accepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean smsAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean contactsAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean bootAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                    if (location1Accepted && location2Accepted && smsAccepted && contactsAccepted && bootAccepted)
                        toastMessage("Permission Granted");
                    else {
                            showMessageOKCancel("You need to allow access to all the permissions", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, SEND_SMS, READ_CONTACTS, RECEIVE_BOOT_COMPLETED},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    });
                            return;
                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

