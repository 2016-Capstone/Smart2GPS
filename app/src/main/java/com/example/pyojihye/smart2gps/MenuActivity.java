package com.example.pyojihye.smart2gps;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import static com.example.pyojihye.smart2gps.Const.ConnectionTrue;
import static com.example.pyojihye.smart2gps.Const.Dronelocation;
import static com.example.pyojihye.smart2gps.Const.IP;
import static com.example.pyojihye.smart2gps.Const.PORT;
import static com.example.pyojihye.smart2gps.Const.PROTO_DVTYPE_KEY;
import static com.example.pyojihye.smart2gps.Const.PROTO_MSG_TYPE_KEY;
import static com.example.pyojihye.smart2gps.Const.bufferedReader;
import static com.example.pyojihye.smart2gps.Const.client;
import static com.example.pyojihye.smart2gps.Const.first;
import static com.example.pyojihye.smart2gps.Const.last;
import static com.example.pyojihye.smart2gps.Const.location;
import static com.example.pyojihye.smart2gps.Const.marker;
import static com.example.pyojihye.smart2gps.Const.printWriter;
import static com.example.pyojihye.smart2gps.Const.start;

/**
 * Created by PYOJIHYE on 2017-05-24.
 */

public class MenuActivity extends AppCompatActivity {

    Button buttonAuto;
    Button buttonManual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        buttonAuto = (Button) findViewById(R.id.buttonAuto);
        buttonManual = (Button) findViewById(R.id.buttonManual);

        buttonAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        buttonManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ControlActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder d = new AlertDialog.Builder(this);
            d.setTitle(getString(R.string.dialog_title));
            d.setMessage(getString(R.string.dialog_contents));
            d.setIcon(R.mipmap.ic_launcher);

            d.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String text;

                    if (ConnectionTrue) {

                        ChatOperator chatOperator = new ChatOperator();
                        last = true;

                        text = protocolSet("32", first);
                        chatOperator.MessageSend(text);

                        text = protocolSet("113", first);
                        chatOperator.MessageSend(text);

                        text = protocolSet("27", first);
                        chatOperator.MessageSend(text);
                    }
                    try {
                        if (!client.isClosed()) {
                            Thread.sleep(100);
                            client.close();
                            first = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });

            d.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            d.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                client = new Socket(IP, PORT);
//                Log.d("Log","IP : "+IP+"\nPORT:"+PORT);

                if (client != null) {
                    ConnectionTrue = true;
                    printWriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader((client.getInputStream()));
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String text = protocolSet("", first);
                    MessageSend(text);
                    first = true;
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.snack_bar_server_port), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.snack_bar_server_connect), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        private void MessageSend(final String text) {
            final Sender messageSender = new Sender();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
            } else {
                messageSender.execute(text);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (client != null) {
                Receiver receiver = new Receiver();
                receiver.execute();
            }
        }
    }

    private String protocolSet(String str, boolean first) {
        String msg = "";
        if (!first) { //첫 연결
            msg = PROTO_DVTYPE_KEY + "=" + Const.PROTO_DVTYPE.PHONE.ordinal() + "%%" + PROTO_MSG_TYPE_KEY + "=" + Const.PROTO_MSGTYPE.HELLO.ordinal();
        } else {
            if (last) { //연결 종료
                msg = PROTO_DVTYPE_KEY + "=" + Const.PROTO_DVTYPE.PHONE.ordinal() + "%%" + PROTO_MSG_TYPE_KEY + "=" + Const.PROTO_MSGTYPE.CMD.ordinal() + "%%DATA=" + str;
            } else { //목적지 위치 알려주기
                msg = PROTO_DVTYPE_KEY + "=" + Const.PROTO_DVTYPE.PHONE.ordinal() + "%%" + PROTO_MSG_TYPE_KEY + "=" + Const.PROTO_MSGTYPE.PATH.ordinal() + "%%DATA=" + str;
            }
        }
        return msg;
    }

    private class Sender extends AsyncTask<String, String, Void> {
        private String message;

        @Override
        protected Void doInBackground(String... params) {
            message = params[0];
            printWriter.write(message + "\n");
            printWriter.flush();
            return null;
        }
    }

    private class Receiver extends AsyncTask<Void, Void, Void> {
        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {
                    if (bufferedReader.ready()) {
                        message = bufferedReader.readLine();
                        publishProgress(null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
