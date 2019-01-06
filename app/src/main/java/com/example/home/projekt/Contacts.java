package com.example.home.projekt;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Contacts extends AppCompatActivity {

    LinearLayout contactView;
    Button btnBack, btnAddContact;
    Database db;

    static final int PICK_CONTACT=1;
    ArrayList<ArrayList<String>> listContacts;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        db = new Database(this);

        contactView = (LinearLayout) findViewById(R.id.layoutVertical);
        listContacts = db.getPhoneNumbersList();
        for(int i=0; i<listContacts.size();i++){
            TextView contact = new TextView(this);
            contact.setText(listContacts.get(i).get(1));
            contact.setId(Integer.parseInt(listContacts.get(i).get(0)));
            contact.setOnEditorActionListener(editorActionListener);
            contactView.addView(contact);
        }

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Contacts.this, MainActivity.class));
            }
        });

        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                                 startActivityForResult(intent, PICK_CONTACT);
                                             }
                                         }
        );
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
           toastMessage("hi");
            return false;
        }
    };

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
