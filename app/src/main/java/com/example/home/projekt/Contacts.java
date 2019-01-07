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
import android.text.style.TextAppearanceSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.awt.font.TextAttribute;
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
            final TextView contact = new TextView(this);
            contact.setText(listContacts.get(i).get(1));
            contact.setId(Integer.parseInt(listContacts.get(i).get(0)));
            contact.setTextAppearance(this,android.R.style.TextAppearance_Material_Display1);
            contact.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(Contacts.this);
                    alert.setTitle(String.format("Want you DELETE contact %s ?", contact.getText()));
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toastMessage("You delete " + contact.getId());
                            db.deletePhoneNumber(contact.getId());
                            startActivity(new Intent(Contacts.this, Contacts.class));
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create().show();
                    return false;
                }
            });
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            String name, number;
                            number = phones.getString(phones.getColumnIndex("data1"));
                            name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            db.insertPhoneNumber(name,number);
                            startActivity(new Intent(Contacts.this, Contacts.class));
                        }
                    }
                }
                break;
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
