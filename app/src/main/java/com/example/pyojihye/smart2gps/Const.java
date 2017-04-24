package com.example.pyojihye.smart2gps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PYOJIHYE on 2017-04-24.
 */

public class Const {
    public static String IP;
    public static int PORT;
    public static List<String> location = new ArrayList<String>();

    public static final String PROTO_DVTYPE_KEY = "DVTYPE";
    public static final String PROTO_MSG_TYPE_KEY = "MSGTYPE";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    public enum PROTO_DVTYPE {
        PHONE, DRONE
    };

    public enum PROTO_MSGTYPE {
        CMD, GPS, PICTURE, HELLO
    };
}
