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

    private Location location;
    private Database db;
    private ArrayList<ArrayList<String>> listContacts;

    private Button btnEditSendMessage, btnEditContacts, btnSendMessage, btnAlarm, btnListening;

    public boolean listening = false, alarm = false;
    private AlarmLoop alarmLoop;
    private ListeningLoop listeningLoop;
    private Intent intentAlarm, intentListening;
    private boolean boundAlarm = false, boundListening = false;

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

        btnListening = (Button) findViewById(R.id.btnListening);
        btnListening.setText("Start listening");
        btnListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listening = setListening(listening);
            }
        });

        btnAlarm = (Button) findViewById(R.id.btnAlarm);
        btnAlarm.setText("Start Alarm");
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm = setAlarm(alarm);
            }
        });
    }

    private void toastMessage(String text) {
        toast.setText(text);
        toast.show();
    }

    private boolean setAlarm(boolean alarm){
        if(alarm){ // wylaczenie alarmu
            stopService(intentAlarm);
            btnAlarm.setText("Start Alarm");
            toastMessage("Alarm stopped");
            stop();
            alarm = false;
        } else { // wystartowanie alarmu
            intentAlarm = new Intent(MainActivity.this, AlarmLoop.class);
            bindService(intentAlarm, alarmServiceConnection, Context.BIND_AUTO_CREATE);
            startService(intentAlarm);
            btnAlarm.setText("Stop Alarm");
            toastMessage("Alarm started");
            alarm = true;
        }
        return alarm;
    }

    private boolean setListening(boolean listening){
        if(listening){ // wylaczenie nasluchiwania
            stopService(intentListening);
            btnListening.setText("Start listening");
            toastMessage("Listening stopped");
            stop();
            listening = false;
        } else {
            intentListening = new Intent(MainActivity.this, ListeningLoop.class);
            bindService(intentListening, listeningServiceConnection, Context.BIND_AUTO_CREATE);
            startService(intentListening);
            btnListening.setText("Stop listening");
            toastMessage("Listening started");
            listening = true;
        }
        return listening;
    }

    public void startAlarm(){
        listening = setListening(listening);
        alarm = setAlarm(alarm);
    }

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

    protected void stop() {
        if (boundAlarm) {
            AlarmLoop.setCallbacks(null); // unregister
            ListeningLoop.setCallbacks(null);
            unbindService(alarmServiceConnection);
            boundAlarm = false;
        }
        if (boundListening) {
            AlarmLoop.setCallbacks(null); // unregister
            ListeningLoop.setCallbacks(null);
            unbindService(listeningServiceConnection);
            boundListening = false;
        }
    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection alarmServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            AlarmLoop.LocalBinder binder = (AlarmLoop.LocalBinder) service;
            alarmLoop = binder.getService();
            boundAlarm = true;
            alarmLoop.setCallbacks(MainActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundAlarm = false;
        }
    };

    private ServiceConnection listeningServiceConnection= new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ListeningLoop.LocalBinder binder1 = (ListeningLoop.LocalBinder) service;
            listeningLoop = binder1.getService();
            boundListening = true;
            listeningLoop.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundListening = false;
        }
    };
}

