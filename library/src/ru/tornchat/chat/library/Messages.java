package ru.tornchat.chat.library;

import java.util.Vector;

public class Messages {

//    /auth_request▲§▼login▲§▼password
//    /auth_accepted▲§▼nickname
//    /auth_denied
//
//    /msg_format_error
//
//    /type_broadcast
//
//    users = {u1, u2, u3}
//
///   userlist§u1§u2§u3

    public static final String DELIMITER =           "▲§▼";
    public static final String AUTH_REQUEST =        "/auth_request";
    public static final String AUTH_ACCEPTED =       "/auth_accepted";
    public static final String AUTH_DENIED =         "/auth_denied";
    public static final String MSG_FORMAT_ERROR =    "/msg_format_error";
    public static final String TYPE_BROADCAST =      "/type_broadcast";
    public static final String TYPE_RANGECAST =      "/rangecast";
    public static final String USER_LIST =           "/userlist";

    public static String getUserList(String users) {
        return USER_LIST + DELIMITER + users;
    }

    public static String getAuthRequest(String login, String password){
        return AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    public static String getAuthAccepted (String nickname){
        return AUTH_ACCEPTED + DELIMITER + nickname;
    }

    public static String getAuthDenied (){
        return AUTH_DENIED;
    }

    public static String getMsgFormatError(String message){
        return MSG_FORMAT_ERROR + DELIMITER + message;
    }

    public static String getTypeBroadcast(String src, String message){
        return TYPE_BROADCAST + DELIMITER + System.currentTimeMillis() +
                DELIMITER + src + DELIMITER + message;
    }

    public static String getTypeRangecast (String message){
        return TYPE_RANGECAST + DELIMITER + message;
    }

}
