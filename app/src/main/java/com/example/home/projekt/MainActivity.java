package com.example.home.projekt;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ServiceCallbacks {

    private Toast toast;
    public boolean listening, alarm;

    private Location location;
    private Database db;
    private ArrayList<ArrayList<String>> listContacts;

    private Button btnEditSendMessage, btnEditContacts, btnSendMessage, btnAlarm, btnListening;

    private AlarmLoop myService;
    private ListeningLoop listeningLoop;
    private Intent intentAlarm, intentListening;
    private boolean bound = false, bound2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        location = new Location(this);
        db = new Database(this);

        btnEditSendMessage = (Button) findViewById(R.id.btnEditSendMessage);
        btnEditSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Statement.class));
            }
        });

        btnEditContacts = (Button) findViewById(R.id.btnEditContacts) ;
        btnEditContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Contacts.class));
            }
        });

        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listContacts = db.getPhoneNumbersList();
                    StringBuffer message = new StringBuffer("You send message to:");
                    for(int i=0;i<listContacts.size();i++){
                        message.append("\n").append(listContacts.get(i).get(1));
                        Sms.SendMessage(String.format("%s My localization is: %s",db.getStatement(),location.getLastLocationString()), listContacts.get(i).get(2) );
                    }
                    toastMessage(message.toString());
            }
        });

        listening = false;
        btnListening = (Button) findViewById(R.id.btnListening);
        btnListening.setText("Start listening");
        btnListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listening){ // wylaczenie nasluchiwania
                    stopService(intentListening);
                    listening = false;
                    btnListening.setText("Start listening");
                    toastMessage("Listening stopped");
                    onStop1();
                } else {
                    intentListening = new Intent(MainActivity.this, ListeningLoop.class);
                    bindService(intentListening, serviceConnection1, Context.BIND_AUTO_CREATE);
                    startService(intentListening);
                    listening = true;
                    btnListening.setText("Stop listening");
                    toastMessage("Listening started");
                }
            }
        });

        alarm = false;
        btnAlarm = (Button) findViewById(R.id.btnAlarm);
        btnAlarm.setText("Start Alarm");
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarm){ // wylaczenie alarmu
                    stopService(intentAlarm);
                    alarm = false;
                    btnAlarm.setText("Start Alarm");
                    toastMessage("Alarm stopped");
                    onStop1();
                } else { // wystartowanie alarmu
                    intentAlarm = new Intent(MainActivity.this, AlarmLoop.class);
                    bindService(intentAlarm, serviceConnection, Context.BIND_AUTO_CREATE);
                    startService(intentAlarm);
                    alarm = true;
                    btnAlarm.setText("Stop Alarm");
                    toastMessage("Alarm started");
                }
            }
        });

    }


    private void toastMessage(String text) {
        toast.setText(text);
        toast.show();
    }

    protected void onStop1() {
        if (bound) {
            AlarmLoop.setCallbacks(null); // unregister
            ListeningLoop.setCallbacks(null);
            unbindService(serviceConnection);
            bound = false;
        }
        if (bound2) {
            AlarmLoop.setCallbacks(null); // unregister
            ListeningLoop.setCallbacks(null);
            unbindService(serviceConnection1);
            bound2 = false;
        }
    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            AlarmLoop.LocalBinder binder = (AlarmLoop.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(MainActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    private ServiceConnection serviceConnection1 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ListeningLoop.LocalBinder binder1 = (ListeningLoop.LocalBinder) service;
            listeningLoop = binder1.getService();
            bound2 = true;
            listeningLoop.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound2 = false;
        }
    };

    public void startAlarm(){
        // wylaczenie nasluchiwania
        stopService(intentListening);
        listening = false;
        btnListening.setText("Start listening");
        toastMessage("Listening stopped");
        onStop1();
        //wlaczenie alarmu
        intentAlarm = new Intent(MainActivity.this, AlarmLoop.class);
        bindService(intentAlarm, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intentAlarm);
        alarm = true;
        btnAlarm.setText("Stop Alarm");
        toastMessage("Alarm started");
    }

    /* Defined by ServiceCallbacks interface */
    @Override
    public void sendMessage() {
        if(!alarm){
            alarm = true;
            btnAlarm.setText("Stop Alarm");
            toastMessage("Alarm started");
        }
        listContacts = db.getPhoneNumbersList();
        StringBuffer message = new StringBuffer("You send message to:");
        for(int i=0;i<listContacts.size();i++){
            message.append("\n").append(listContacts.get(i).get(1));
            Sms.SendMessage(String.format("%s My localization is: %s",db.getStatement(),location.getLastLocationString()), listContacts.get(i).get(2) );
        }
        toastMessage(message.toString());
    }
}

