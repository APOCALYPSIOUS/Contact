package com.example.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_CONTACT_REQUEST = 1;
    private Uri contact;
    private TextView display;
    private TextView txtname;
    private TextView txtphone;
    private Button detailcontact;
    private Button call;
    private int id;
    private int Perm_CTC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detailcontact = (Button) findViewById(R.id.detailscontact);
        detailcontact.setEnabled(false);
        call = (Button) findViewById(R.id.call);
        call.setEnabled(false);
        Button contacts = (Button) findViewById(R.id.contactid);
        display = findViewById(R.id.textView);
        txtname = findViewById(R.id.name);
        txtphone= findViewById(R.id.phone);
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.READ_CONTACTS},Perm_CTC);
                Uri uri = Uri.parse("content://contacts/people");
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, uri);
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);

            }
        });
        detailcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.READ_CONTACTS},Perm_CTC);

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Perm_CTC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String contactPhone;
                String contactName;
                String contactId;

                Cursor cursor = getContentResolver().query(Uri.parse(String.valueOf(contact)), null, null, null, null);
                if (cursor.moveToNext()){
                    do {
                        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    }while(cursor.moveToNext());
                    Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?"+" and " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE + "=" +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                            new String[]{contactId},
                            null);

                    if (cursorPhone.moveToNext()) {
                        contactPhone = cursorPhone.getString(0);

                        txtphone.setText("Phone :" +contactPhone);
                        contact = Uri.parse(contactPhone);
                    }
                    Log.i("TAG", "onRequestPermissionsResult: ");
                    display.setText("Id: "+ contactId);
                    txtname.setText("Name: "+ contactName);
                }

                //Toast.makeText(this, "GRANTED CALL",
                Toast.makeText(MainActivity.this,"GRANTED CALL",Toast.LENGTH_SHORT).show();

                call.setEnabled(true);
            }
        }
    }
    public void getid(Intent data){
       contact= Uri.parse(data.getDataString());
        id=Integer.parseInt(contact.getLastPathSegment());
        display.setText(String.valueOf(id));
        detailcontact.setEnabled(true);

    }
    public void callnumber(String phone){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed as usual
        } else {
            // Permission has been revoked, show a message to the user
            Toast.makeText(MainActivity.this, "Call phone permission has been revoked", Toast.LENGTH_SHORT).show();
        }

        Uri uri = Uri.parse("tel:" + phone);

        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        startActivity(intent);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                getid(data);






            }
        }


    }

}