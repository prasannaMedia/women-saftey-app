package com.dan.naari;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
//import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.app.PendingIntent;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

public class AddRelative extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private static final int REQUEST_SMS = 1;
    DatabaseHelper myDB;
    Button btnAdd,btnView;
    EditText editText, editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relative);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnView = (Button) findViewById(R.id.btnView);
        myDB = new DatabaseHelper(this);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editText.getText().toString();
                String newEntry1 = editText2.getText().toString();
                if(editText.length()!= 0){
                    AddData(newEntry);
                    AddData(newEntry1);
                    editText.setText("");
                }else{
                    Toast.makeText(AddRelative.this, "You must put something in the text field!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddRelative.this, ViewListContents.class);
                startActivity(intent);
            }
        });


    }

    public void AddData(String newEntry) {

        boolean insertData = myDB.addData(newEntry);

        if(insertData==true){
            Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action, keycode;

        action = event.getAction();
        String Number;
        int count=0;
        keycode = event.getKeyCode();
        switch (keycode)
        {
            case KeyEvent.KEYCODE_VOLUME_UP:
                {
                if(KeyEvent.ACTION_UP == action){

                    ArrayList<String> theList = new ArrayList<>();
                    Cursor data = myDB.getListContents();
                    if(data.getCount() == 0){
                        Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
                    }else {
                        while (data.moveToNext()) {
                            theList.add(data.getString(1));

                        }
                    }
                    System.out.println(theList);
                    int counter=1;
                    while (counter<theList.size()){
                        System.out.println(counter);
                        Number=theList.get(counter);
                        System.out.println(Number);
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + Number.trim()));
                        if (ActivityCompat.checkSelfPermission(AddRelative.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddRelative.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                            startActivity(callIntent);
                        } else {
                            callIntent.setData(Uri.parse("tel:" + Number.trim()));
                            startActivity(callIntent);
                        }
                        if (ActivityCompat.checkSelfPermission(AddRelative.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddRelative.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS);
                            Intent intent=new Intent(getApplicationContext(),AddRelative.class);
                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
                            SmsManager sms=SmsManager.getDefault();
                            sms.sendTextMessage(Number.trim(), null, "hello i am in emergency situation my location is lat lng", pi,null);
                        } else {
                            Intent intent=new Intent(getApplicationContext(),AddRelative.class);
                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
                            SmsManager sms=SmsManager.getDefault();
                            sms.sendTextMessage(Number.trim(), null, "hello i am in emergency situation my location is lat lng", pi,null);
                        }
                        startActivity(callIntent);
                        counter +=2;
                    }

                }
            }
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(KeyEvent.ACTION_DOWN == action){
                    //count = 0;
                    //String S2 = String.valueOf(count);
                    //Log.d("downButton", S2);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + "9482090700"));
                    if (ActivityCompat.checkSelfPermission(AddRelative.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddRelative.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

                    } else {
                        callIntent.setData(Uri.parse("tel:"));
                        startActivity(callIntent);
                    }
                    startActivity(callIntent);
                }
        }
        return super.dispatchKeyEvent(event);
    }
}

