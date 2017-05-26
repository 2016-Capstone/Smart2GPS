package com.example.pyojihye.smart2gps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PYOJIHYE on 2017-04-24.
 */

public class Const {
    public static String IP;
    public static int PORT;

    public static Marker marker;

    public static List<String> location = new ArrayList<String>();

    public static final String PROTO_DVTYPE_KEY = "DVTYPE";
    public static final String PROTO_MSG_TYPE_KEY = "MSGTYPE";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static final int DEFAULT_ZOOM_LEVEL = 18;

    public static boolean ConnectionTrue;
    public static boolean first;
    public static boolean last;
    public static boolean start;

    public static Socket client;

    public static PrintWriter printWriter;
    public static BufferedReader bufferedReader;

    public static LatLng Dronelocation;

    public enum PROTO_DVTYPE {
        PHONE, DRONE
    }

    ;

    public enum PROTO_MSGTYPE {
        CMD, GPS, IMG, HELLO, PATH
    }

    ;
}
