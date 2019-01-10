package com.example.home.projekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toast toast;
    public boolean listening, alarm;

    private Location location;
    private Database db;
    private ArrayList<ArrayList<String>> listContacts;

    private Button btnEditSendMessage, btnEditContacts, btnSendMessage, btnAlarm, btnListening;

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

        listening = true;
        startService(new Intent(MainActivity.this, ListeningLoop.class));
        btnListening = (Button) findViewById(R.id.btnListening);
        btnListening.setText("Stop listening");
        btnListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listening){
                    stopService(new Intent(MainActivity.this, ListeningLoop.class));
                    listening = false;
                    btnListening.setText("Start listening");
                    toastMessage("Listening stopped");
                } else {
                    startService(new Intent(MainActivity.this, ListeningLoop.class));
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
                if(alarm){
                    stopService(new Intent(MainActivity.this, AlarmLoop.class));
                    alarm = false;
                    btnAlarm.setText("Start Alarm");
                    toastMessage("Alarm stopped");
                } else {
                    startService(new Intent(MainActivity.this, AlarmLoop.class));
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
}

