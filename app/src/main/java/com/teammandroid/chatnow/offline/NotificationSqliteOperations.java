package com.teammandroid.chatnow.offline;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.offline.core.DatabaseAccess;

import java.util.ArrayList;

public class NotificationSqliteOperations {

    private static final String TAG = NotificationSqliteOperations.class.getSimpleName();

    /***TYPE==1(sent notifications)
     *  TYPE==2(received Notification)
    ***/

    private DatabaseAccess database;

    private String TABLE_NAME = "notification";

    private Context context;

    public NotificationSqliteOperations(Context context) {
        try {
            context = context;
            this.database = DatabaseAccess.getInstance(context);
        } catch (Exception ex) {
            Log.e(TAG, "NotificationSqliteOperations: ", ex);
        }
    }

    public long saveNotification(NotificationModel notification) {
        Log.e(TAG, "saveNotification: ");
        long response = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("message", notification.getMessage());
            contentValues.put("msgtime", notification.getMsgtime());
            contentValues.put("isread", notification.getIsread());
            contentValues.put("type", notification.getType());
            contentValues.put("registrationid", notification.getSenderid());
            response = database.insert(TABLE_NAME, contentValues);
        } catch (Exception ex) {
            Log.e(TAG, "saveQuizStatus: ", ex);
        } finally {
            database.close();
        }
        return response;
    }

    public ArrayList<NotificationModel> GetNotifications() {
        Log.e(TAG, "GetNotifications: ");

        ArrayList<NotificationModel> notificationList = new ArrayList<>();

        try {
            Cursor dbCcursor = database.rawQuery("select * from " + TABLE_NAME, null);
            Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    NotificationModel notification = new NotificationModel();
                    notification.setMessageid(dbCcursor.getInt(0));
                    notification.setMessage(dbCcursor.getString(1));
                    notification.setMsgtime(dbCcursor.getString(2));
                    notification.setIsread(dbCcursor.getInt(3));
                    notification.setType(dbCcursor.getInt(4));
                    notificationList.add((notification));
                    //Log.e("GetBookings: ", itemsSqlite.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetStudents: ", ex);
                }
            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParent: ", ex);
        }
        Log.e("GetBookings: ", notificationList.toString());
        return notificationList;
    }

    public ArrayList<NotificationModel> GetUnReadNotifications() {
        Log.e(TAG, "GetUnReadNotifications: ");

        ArrayList<NotificationModel> notificationList = new ArrayList<>();

        try {
            Cursor dbCcursor = database.rawQuery("select * from notification where isread=0 and type=2", null); // change type = 2 when proper trainer and customer entries done
            Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    NotificationModel notification = new NotificationModel();
                    notification.setMessageid(dbCcursor.getInt(0));
                    notification.setMessage(dbCcursor.getString(1));
                    notification.setMsgtime(dbCcursor.getString(2));
                    notification.setIsread(dbCcursor.getInt(3));
                    notification.setType(dbCcursor.getInt(4));
                    notification.setSenderid(dbCcursor.getInt(5));
                    notificationList.add((notification));
                    //Log.e("GetBookings: ", itemsSqlite.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetURNotifications: ", ex);
                }

            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParent: ", ex);
        }
        Log.e("GetURNotifications: ", notificationList.toString());
        return notificationList;
    }

    public ArrayList<NotificationModel> GetUnReadNotificationsfromUserId(int userId) {
        Log.e(TAG, "GetUnReadNotifications: ");

        ArrayList<NotificationModel> notificationList = new ArrayList<>();

        try {
            Cursor dbCcursor = database.rawQuery("select * from notification where isread=0 and type=2 and registrationid="+userId+"", null); // change type = 2 when proper trainer and customer entries done
          //  Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    NotificationModel notification = new NotificationModel();
                    notification.setMessageid(dbCcursor.getInt(0));
                    notification.setMessage(dbCcursor.getString(1));
                    notification.setMsgtime(dbCcursor.getString(2));
                    notification.setIsread(dbCcursor.getInt(3));
                    notification.setType(dbCcursor.getInt(4));
                    notification.setSenderid(dbCcursor.getInt(5));
                    notificationList.add((notification));
                    //Log.e("GetBookings: ", itemsSqlite.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetURNotifications: "+ ex.getMessage());
                }

            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParent: "+ ex.getMessage());
        }
        //Log.e("GetURNotifications: ", notificationList.toString());
        return notificationList;
    }

   /* public ItemsSqlite GetNotification(int messageid) {
        Log.e(TAG, "GetBooking: ");

        ItemsSqlite itemsSqlite = new ItemsSqlite();

        try {

            Cursor dbCcursor = database.rawQuery("select * from notification where messageid=" + messageid, null);
            try {

                if (dbCcursor.moveToFirst()) do {

                    itemsSqlite.setBookingid(dbCcursor.getInt(0));
                    itemsSqlite.setItemid(dbCcursor.getInt(1));
                    itemsSqlite.setServiceid(dbCcursor.getInt(2));
                    itemsSqlite.setCategoryid(dbCcursor.getInt(3));
                    itemsSqlite.setItemname(dbCcursor.getString(4));
                    itemsSqlite.setDescription(dbCcursor.getString(5));
                    itemsSqlite.setPrice(dbCcursor.getDouble(6));
                    itemsSqlite.setOffer(dbCcursor.getDouble(7));
                    itemsSqlite.setOfferprice(dbCcursor.getDouble(8));
                    itemsSqlite.setNumber(dbCcursor.getInt(9));
                    itemsSqlite.setTotalprice(dbCcursor.getDouble(10));
                } while (dbCcursor.moveToNext());
            } catch (Exception ex) {
                Log.e( "itemsSqlite: ", itemsSqlite.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemsSqlite;
    }*/

    public long readAllNotifications() {
            Log.e(TAG, "readAllNotifications: ");
            long response = 0;

            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("isread",1);
                response = database.update(TABLE_NAME, contentValues,null , null);
            } catch (Exception ex) {
                Log.e(TAG, "readAllNotifications: ", ex);
            } finally {
                database.close();
            }

            return response;
    }


    public long readAllNotificationsForUser(int partnerId) {
        Log.e(TAG, "readAllNotifications: ");
        long response = 0;

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("isread",1);
            response = database.update(TABLE_NAME, contentValues,"registrationid="+partnerId , null);
        } catch (Exception ex) {
            Log.e(TAG, "readAllNotifications: ", ex);
        } finally {
            database.close();
        }

        return response;
    }


    public long deleteNotifications(int messageid ) {
        Log.e(TAG, "deleteNotifications: ");
        long response = 0;

        try {
            //database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{contact.getID()});
            response = database.delete(TABLE_NAME, "messageid = ?", new String[]{String.valueOf(messageid)});
        } catch (Exception ex) {
            Log.e(TAG, "deleteNotifications: ", ex);
        } finally {
            database.close();
        }

        return response;
    }

}
