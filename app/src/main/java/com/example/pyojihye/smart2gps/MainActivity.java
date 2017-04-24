package com.example.pyojihye.smart2gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import static com.example.pyojihye.smart2gps.Const.IP;
import static com.example.pyojihye.smart2gps.Const.PORT;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private EditText editTextIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextIP = (EditText) findViewById(R.id.editTextIP);
    }

    public void onButtonConnectClicked(View v) {
        if (editTextIP.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_ip), Toast.LENGTH_LONG).show();
        } else {
            try {
                setSocket(editTextIP.getText().toString());
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setSocket(String editTextIP) {
        IP = editTextIP.substring(0, editTextIP.lastIndexOf(":"));
        PORT = Integer.parseInt(editTextIP.substring(editTextIP.lastIndexOf(":") + 1, editTextIP.length()));
    }
}
