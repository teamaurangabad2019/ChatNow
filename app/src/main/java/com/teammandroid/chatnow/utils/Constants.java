package com.teammandroid.chatnow.utils;

public class Constants {

    //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text

    public static final String UPLOAD_URL = "";
    public static final String OFFLINE_IMAGE_PATH = "Chatnow/Media/Chatnow Images/";
    public static final String OFFLINE_AUDIO_PATH = "Chatnow/Media/Chatnow Audio/";
    public static final String OFFLINE_VIDEO_PATH = "Chatnow/Media/Chatnow Videos/";
    public static final String OFFLINE_DOCUMENT_PATH = "Chatnow/Media/Chatnow Documents/";
    public static final String OFFLINE_GIF_PATH = "Chatnow/Media/Chatnow Gif/";

    //http://domaincomputer.co.in/chatnowapp/api/processes/message.php/
    static final String BASE_HOST_NAME = "http://domaincomputer.co.in";
    static final String BASE_HOST_IP = "http://192.168.2.29";
    private static final String BASE_ROUTE_END_POINT = "/dashboard/api/processes";

    private static final String BASE_API_END_POINT = BASE_HOST_NAME + BASE_ROUTE_END_POINT;

    public static final String FIRE_BASE_TOKEN_PREF = "FbTokenIdPref";
    public static final String FCM_API  = "https://fcm.googleapis.com/fcm/send";
    public static final String GET_SERVER_KEY="key=AAAAIjQwVdQ:APA91bFoJDsnBiKPhWjPo2rnzTJ7x1MmOLzbZZmxrSukM2b-s3JbBw5KIsRVQXiyGnJYRdZB9NPt27GMj-cP87jb9o0w8-nm9e1tkGtocnVM7DbnWoGaDQWlxUlgun0-gRC71JiOuy_C";
    //public static final String CHATTING = BASE_API_END_POINT +"/message.php/" ;
    public static final String CHATTING = "http://domaincomputer.co.in/chatnowapp/api/processes/message.php/" ;
    // public static final String URL_LOGIN =BASE_HOST_IP+BASE_ROUTE_END_POINT+"/user.php/";
    public static final String URL_LOGIN ="http://domaincomputer.co.in/chatnowapp/api/processes/user.php/";
    public static final String URL_USER_PROFILE_PIC ="http://domaincomputer.co.in/chatnowapp/api/attachments/profilepic/";
    public static final String URL_CHATTING_MEDIA ="http://domaincomputer.co.in/chatnowapp/api/attachments/PDF/";

}
