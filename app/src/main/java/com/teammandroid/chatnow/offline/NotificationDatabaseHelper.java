package com.teammandroid.chatnow.offline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.offline.core.DatabaseAccess;

import java.util.ArrayList;

public class NotificationDatabaseHelper {
    private static final String TAG = NotificationDatabaseHelper.class.getSimpleName();

    public final static String DATABASE_NAME = "chatnowdb.db";

    public final static String TABLE_NOTIFICATION = "notification";

    public final static String COL_1 = "messageid";
    public final static String COL_2 = "firebaseid";
    public final static String COL_3 = "receiverid";
    public final static String COL_4 = "senderid";
    public final static String COL_5 = "message";
    public final static String COL_6 = "msgtime";
    public final static String COL_7 = "isread";
    public final static String COL_8 = "isdownloaded"; // 0 if not downloaded //1 downloaded
    public final static String COL_9 = "mediatype"; //1 img //2 gif //3 audio //4 video //5 document //6 url // 7 text
    public final static String COL_10 = "type";  // 2 for incoming msg and 1 for outgoing msg
    public final static String COL_11 = "replyToMsgId";
    public final static String COL_12 = "replyToMsgSenderId";
    public final static String COL_13 = "replyToMsgText";
    public final static String COL_14 = "replyToMsgTextMediaType";

    private SQLiteDatabase database;
    private static DatabaseAccess databaseAccess;
    private static final int DATABASE_VERSION = 2;

    Context context;
    public NotificationDatabaseHelper(@Nullable Context context) {
        try {
            this.databaseAccess = new DatabaseAccess(context);
            // super(context, DATABASE_NAME, null, DATABASE_VERSION);
        } catch (Exception ex) {
            Log.e(TAG, "NotificationSqliteOperations: ", ex);
        }
    }

    public long saveNotification(NotificationModel notification) {

        SQLiteDatabase db = databaseAccess.getWritableDatabase();

        Log.e(TAG, "saveNotification: ");

        long response = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("firebaseid", notification.getFirebaseid());
            contentValues.put("receiverid", notification.getReceiverid());
            contentValues.put("senderid", notification.getSenderid());
            contentValues.put("message", notification.getMessage());
            contentValues.put("msgtime", notification.getMsgtime());
            contentValues.put("isread", notification.getIsread());
            contentValues.put("isdownloaded", notification.getIsdownloaded());
            contentValues.put("mediatype", notification.getMediatype());
            contentValues.put("type", notification.getType());
            contentValues.put("replyToMsgId", notification.getReplyToMsgId());
            contentValues.put("replyToMsgSenderId", notification.getReplyToMsgSenderId());
            contentValues.put("replyToMsgText", notification.getReplyToMsgText());
            contentValues.put("replyToMsgTextMediaType", notification.getReplyToMsgTextMediaType());
            response = db.insert(TABLE_NOTIFICATION, null, contentValues);
        } catch (Exception ex) {
            Log.e(TAG, "saveQuizStatus: ", ex);
        } finally {
            db.close();
        }
        return response;
    }

    public Integer updateData(NotificationModel notification) {

        SQLiteDatabase db = databaseAccess.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //int chattingId=getUserId(senderId,receiverId);
        contentValues.put(COL_2, notification.getFirebaseid());
        contentValues.put(COL_3, notification.getReceiverid());
        contentValues.put(COL_4, notification.getSenderid());
        contentValues.put(COL_5, notification.getMessage());
        contentValues.put(COL_6, notification.getMsgtime());
        contentValues.put(COL_7, notification.getIsread());
        contentValues.put(COL_8, notification.getIsdownloaded());
        contentValues.put(COL_9, notification.getMediatype());
        contentValues.put(COL_10, notification.getType());
        contentValues.put(COL_11, notification.getReplyToMsgId());
        contentValues.put(COL_12, notification.getReplyToMsgSenderId());
        contentValues.put(COL_13, notification.getReplyToMsgText());
        contentValues.put(COL_14, notification.getReplyToMsgText());

        /*return db.update(TABLE_CHATTING, contentValues,COL_2+"=? AND" +COL_4 + "=?",
                new String[]{String.valueOf(senderId),String.valueOf(receiverId)});*/

        String WHERE = "firebaseid='" + notification.getFirebaseid()+"'";
        return db.update(TABLE_NOTIFICATION, contentValues, WHERE, null);

    }

    public ArrayList<NotificationModel> GetNotifications() {
        Log.e(TAG, "GetNotifications: ");
        SQLiteDatabase db = databaseAccess.getWritableDatabase();

        ArrayList<NotificationModel> notificationList = new ArrayList<>();

        try {
            Cursor dbCcursor = db.rawQuery("select * from " + TABLE_NOTIFICATION, null);
            Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    NotificationModel notification = new NotificationModel();
                    notification.setMessageid(dbCcursor.getInt(0));
                    notification.setFirebaseid(dbCcursor.getString(1));
                    notification.setReceiverid(dbCcursor.getInt(2));
                    notification.setSenderid(dbCcursor.getInt(3));
                    notification.setMessage(dbCcursor.getString(4));
                    notification.setMsgtime(dbCcursor.getString(5));
                    notification.setIsread(dbCcursor.getInt(6));
                    notification.setIsdownloaded(dbCcursor.getInt(7));
                    notification.setMediatype(dbCcursor.getInt(8));
                    notification.setType(dbCcursor.getInt(9));
                    notification.setReplyToMsgId(dbCcursor.getInt(10));
                    notification.setReplyToMsgSenderId(dbCcursor.getInt(11));
                    notification.setReplyToMsgText(dbCcursor.getString(12));
                    notification.setReplyToMsgTextMediaType(dbCcursor.getInt(13));
                    notificationList.add((notification));
                    //Log.e("GetBookings: ", itemsSqlite.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetAllNotifications: ", ex);
                }
            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParentNotifi: ", ex);
        }
        Log.e("GetAllNotification: ", notificationList.toString());
        return notificationList;
    }

    public ArrayList<NotificationModel> GetUnReadNotificationsfromUserId(int userId) {
        Log.e(TAG, "GetUnReadNotifications: ");
        SQLiteDatabase database = databaseAccess.getWritableDatabase();

        ArrayList<NotificationModel> notificationList = new ArrayList<>();

        try {
            Cursor dbCcursor = database.rawQuery("select * from notification where isread=0 and type=2 and senderid=" + userId + "", null); // change type = 2 when proper trainer and customer entries done
            //  Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    NotificationModel notification = new NotificationModel();
                    notification.setMessageid(dbCcursor.getInt(0));
                    notification.setFirebaseid(dbCcursor.getString(1));
                    notification.setReceiverid(dbCcursor.getInt(2));
                    notification.setSenderid(dbCcursor.getInt(3));
                    notification.setMessage(dbCcursor.getString(4));
                    notification.setMsgtime(dbCcursor.getString(5));
                    notification.setIsread(dbCcursor.getInt(6));
                    notification.setIsdownloaded(dbCcursor.getInt(7));
                    notification.setMediatype(dbCcursor.getInt(8));
                    notification.setType(dbCcursor.getInt(9));
                    notification.setReplyToMsgId(dbCcursor.getInt(10));
                    notification.setReplyToMsgSenderId(dbCcursor.getInt(11));
                    notification.setReplyToMsgText(dbCcursor.getString(12));
                    notification.setReplyToMsgTextMediaType(dbCcursor.getInt(13));

                    notificationList.add((notification));
                    //Log.e("GetBookings: ", itemsSqlite.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetURNotifications: " + ex.getMessage());
                }

            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetUNRParent: " + ex.getMessage());
        }
        //Log.e("GetURNotifications: ", notificationList.toString());
        return notificationList;
    }

    public long readAllNotificationsForUser(int partnerId) {
        Log.e(TAG, "readAllNotifications: ");

        SQLiteDatabase database = databaseAccess.getWritableDatabase();

        long response = 0;

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("isread", 1);
            response = database.update(TABLE_NOTIFICATION, contentValues, "senderid=" + partnerId, null);
        } catch (Exception ex) {
            Log.e(TAG, "readAllNotifications: ", ex);
        } finally {
            database.close();
        }

        return response;
    }

    public ArrayList<NotificationModel> GetNotificationsBitweenUsers(int senderid, int receiverid) {
        Log.e(TAG, "GetAllMessageNotifications: ");
        SQLiteDatabase db = databaseAccess.getWritableDatabase();

        ArrayList<NotificationModel> notificationList = new ArrayList<>();
        try {

           /* SELECT * FROM message_chat WHERE senderid=1 AND receiverid =2
            OR  receiverid=1 AND senderid =2 ORDER BY created DESC'*/
            Cursor dbCcursor = db.rawQuery("SELECT * FROM notification WHERE senderid= '" + senderid + "' AND receiverid='" +receiverid +
                    "' OR senderid='" + receiverid + "' AND receiverid='" +senderid +"' ORDER BY msgtime DESC ", null);
            Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    NotificationModel notification = new NotificationModel();
                    notification.setMessageid(dbCcursor.getInt(0));
                    notification.setFirebaseid(dbCcursor.getString(1));
                    notification.setReceiverid(dbCcursor.getInt(2));
                    notification.setSenderid(dbCcursor.getInt(3));
                    notification.setMessage(dbCcursor.getString(4));
                    notification.setMsgtime(dbCcursor.getString(5));
                    notification.setIsread(dbCcursor.getInt(6));
                    notification.setIsdownloaded(dbCcursor.getInt(7));
                    notification.setMediatype(dbCcursor.getInt(8));
                    notification.setType(dbCcursor.getInt(9));
                    notification.setReplyToMsgId(dbCcursor.getInt(10));
                    notification.setReplyToMsgSenderId(dbCcursor.getInt(11));
                    notification.setReplyToMsgText(dbCcursor.getString(12));
                    notification.setReplyToMsgTextMediaType(dbCcursor.getInt(13));

                    notificationList.add((notification));
                    //Log.e("GetBookings: ", itemsSqlite.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetAllNotifications: ", ex);
                }

            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParentNotifi: ", ex);
        }
        Log.e("GetAllNotification: ", notificationList.toString());
        return notificationList;
    }
}
