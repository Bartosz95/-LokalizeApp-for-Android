package com.example.home.projekt;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Location location;
    private Database db;
    ArrayList<ArrayList<String>> listContacts;

    private int timeLoop, timeLoopNormal = 2000, timeLoopAlarm = 60*1000, timeWait, timeWaitNormal = 5000, countButtonPush = 0, activateCountButtonPush = 10;
    boolean alarm = false;
    Handler handler;

    private Button btnEditSendMessage, btnEditContacts, btnSendMessage, btnAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = new Location(this);
        db = new Database(this);

        timeLoop=timeLoopNormal;
        timeWait=timeWaitNormal;
        handler = new Handler(getApplicationContext().getMainLooper());
        loop();

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

        btnAlarm = (Button) findViewById(R.id.btnAlarm);
        btnAlarm.setText("Start Alarm");
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarm){
                    countButtonPush=0;

                    timeWait=timeWaitNormal;
                    timeLoop=timeLoopNormal;
                    btnAlarm.setText("Start Alarm");
                    alarm = false;
                } else {
                    countButtonPush=0;
                    timeWait=0;
                    timeLoop=timeLoopAlarm;
                    btnAlarm.setText("Stop Alarm");
                    alarm = true;
                }
            }
        });

    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    countButtonPush++;
                }
                return true;
            default:

                return super.dispatchKeyEvent(event);
        }
    }

    private void loop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(timeLoop);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if((countButtonPush>0) || alarm){
                        try {
                            Thread.sleep(timeWait);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if((countButtonPush>activateCountButtonPush-1) || alarm){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(!alarm){
                                        timeWait=0;
                                        timeLoop=timeLoopAlarm;
                                        btnAlarm.setText("Stop Alarm");
                                        alarm = true;
                                    }
                                    listContacts = db.getPhoneNumbersList();
                                    StringBuffer message = new StringBuffer("Sended message to:");
                                    for(int i=0;i<listContacts.size();i++){
                                        message.append("\n").append(listContacts.get(i).get(1));
                                        Sms.SendMessage(String.format("%s My localization is: %s",db.getStatement(),location.getLastLocationString()), listContacts.get(i).get(2) );
                                    }
                                    toastMessage(message.toString());
                                }
                            });
                        } else {
                            countButtonPush=0;
                        }
                    }
                }
            }
        }).start();
    }
}

