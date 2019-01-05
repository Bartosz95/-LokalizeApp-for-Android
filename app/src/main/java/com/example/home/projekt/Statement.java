package com.example.home.projekt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Statement extends AppCompatActivity {

    private Button btnBack;
    private EditText textStatement;

    Database db;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement);

        db = new Database(this);
        intent = new Intent(this, MainActivity.class);

        textStatement = (EditText) findViewById(R.id.editTextStatement);
        textStatement.setText(db.getStatement());
        textStatement.setOnEditorActionListener(editorActionListener);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            db.editStatement(textStatement.getText().toString());
            startActivity(intent);
            return false;
        }
    };


    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}