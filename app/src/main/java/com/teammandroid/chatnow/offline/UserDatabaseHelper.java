package com.teammandroid.chatnow.offline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.teammandroid.chatnow.models.OfflineUserModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.offline.core.DatabaseAccess;
import com.teammandroid.chatnow.utils.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

public class UserDatabaseHelper {

    private static final String TAG = UserDatabaseHelper.class.getSimpleName();
    public final static String DATABASE_NAME = "chatnowdb.db";
    public final static String TABLE_USER = "users";
    public final static String COL_1 = "uid";
    public final static String COL_2 = "userid";
    public final static String COL_3 = "fullname";
    public final static String COL_4 = "roleid";
    public final static String COL_5 = "address";
    public final static String COL_6 = "mobile";
    public final static String COL_7 = "token";
    public final static String COL_8 = "email";
    public final static String COL_9 = "profilepic";
    public final static String COL_10 = "latitute";
    public final static String COL_11 = "longitude";
    public final static String COL_12 = "device";
    public final static String COL_13 = "isactive";
    public final static String COL_14 = "created";
    public final static String COL_15= "createdby";
    public final static String COL_16 = "modified";
    public final static String COL_17 = "modifiedby";

    private SQLiteDatabase database;
    private static DatabaseAccess databaseAccess;
    private static final int DATABASE_VERSION = 2;

    Context context;

    public UserDatabaseHelper(@Nullable Context context) {
        try {
            this.databaseAccess = new DatabaseAccess(context);
            // super(context, DATABASE_NAME, null, DATABASE_VERSION);
        } catch (Exception ex) {
            Log.e(TAG, "UserSqliteOperations: ", ex);
        }
    }

    public long saveUser(UserModel userModel) {

        SQLiteDatabase db = databaseAccess.getWritableDatabase();

        Log.e(TAG, "saveNotification: ");

        long response = 0;
        try {
            byte[] image = getBlobImage(Constants.URL_USER_PROFILE_PIC + userModel.getProfilepic());

          //  if (image != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COL_2, userModel.getUserid());
                contentValues.put(COL_3, userModel.getFullname());
                contentValues.put(COL_4, userModel.getRoleid());
                contentValues.put(COL_5, userModel.getAddress());
                contentValues.put(COL_6, userModel.getMobile());
                contentValues.put(COL_7, userModel.getToken());
                contentValues.put(COL_8, userModel.getEmail());
                contentValues.put(COL_9, userModel.getProfilepic());
                contentValues.put(COL_10, userModel.getLatitute());
                contentValues.put(COL_11, userModel.getLongitude());
                contentValues.put(COL_12, userModel.getDevice());
                contentValues.put(COL_13, userModel.getIsactive());
                contentValues.put(COL_14, userModel.getCreated());
                contentValues.put(COL_15, userModel.getCreatedby());
                contentValues.put(COL_16, userModel.getModified());
                contentValues.put(COL_17, userModel.getModifiedby());
                response = db.insert(TABLE_USER, null, contentValues);

                Log.e(TAG, "saveUser: response " + response);
            //}
        } catch (Exception ex) {
            Log.e(TAG, "saveQuizStatus: ", ex);
        } finally {
            db.close();
        }
        return response;
    }

    public Integer updateData(int id, UserModel userModel) {

        int result = 0;
        SQLiteDatabase db = databaseAccess.getWritableDatabase();
        byte[] image = getBlobImage(Constants.URL_USER_PROFILE_PIC + userModel.getProfilepic());

       // if (image != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, userModel.getUserid());
            contentValues.put(COL_3, userModel.getFullname());
            contentValues.put(COL_4, userModel.getRoleid());
            contentValues.put(COL_5, userModel.getAddress());
            contentValues.put(COL_6, userModel.getMobile());
            contentValues.put(COL_7, userModel.getToken());
            contentValues.put(COL_8, userModel.getEmail());
            contentValues.put(COL_9, userModel.getProfilepic());
            contentValues.put(COL_10, userModel.getLatitute());
            contentValues.put(COL_11, userModel.getLongitude());
            contentValues.put(COL_12, userModel.getDevice());
            contentValues.put(COL_13, userModel.getIsactive());
            contentValues.put(COL_14, userModel.getCreated());
            contentValues.put(COL_15, userModel.getCreatedby());
            contentValues.put(COL_16, userModel.getModified());
            contentValues.put(COL_17, userModel.getModifiedby());

        /*return db.update(TABLE_CHATTING, contentValues,COL_2+"=? AND" +COL_4 + "=?",
                new String[]{String.valueOf(senderId),String.valueOf(receiverId)});*/

            String WHERE = "userid='" + id + "'";
            return db.update(TABLE_USER, contentValues, WHERE, null);

       /* }
        return result;*/
    }

    public ArrayList<OfflineUserModel> GetUserid() {
        Log.e(TAG, "GetUser: ");
        SQLiteDatabase db = databaseAccess.getWritableDatabase();

        ArrayList<OfflineUserModel> userList = new ArrayList<>();

        try {
            Cursor dbCcursor = db.rawQuery("select * from " + TABLE_USER, null);
            Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    OfflineUserModel userModel = new OfflineUserModel();
                    userModel.setUid(dbCcursor.getInt(0));
                    userModel.setUserid(dbCcursor.getInt(1));
                    userModel.setFullname(dbCcursor.getString(2));
                    userModel.setRoleid(dbCcursor.getInt(3));
                    userModel.setAddress(dbCcursor.getString(4));
                    userModel.setMobile(dbCcursor.getString(5));
                    userModel.setToken(dbCcursor.getString(6));
                    userModel.setEmail(dbCcursor.getString(7));
                    userModel.setProfilepic(dbCcursor.getString(8));
                    userModel.setLatitute(dbCcursor.getDouble(9));
                    userModel.setLongitude(dbCcursor.getDouble(10));
                    userModel.setDevice(dbCcursor.getString(11));
                    userModel.setIsactive(dbCcursor.getInt(12));
                    userModel.setCreated(dbCcursor.getString(13));
                    userModel.setCreatedby(dbCcursor.getInt(14));
                    userModel.setModified(dbCcursor.getString(15));
                    userModel.setModifiedby(dbCcursor.getInt(16));

                    userList.add((userModel));
                    //Log.e("GetBookings: ", itemsSqlite.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetAllUser: ", ex);
                }
            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParentNotifi: ", ex);
        }
        Log.e("GetAllUser: ", userList.toString());
        return userList;
    }

    public OfflineUserModel GetUserDetails(int userid) {
        Log.e(TAG, "getUser: ");
        SQLiteDatabase db = databaseAccess.getWritableDatabase();
        OfflineUserModel userModel = new OfflineUserModel();

        try {
           /* SELECT * FROM message_chat WHERE senderid=1 AND receiverid =2
            OR  receiverid=1 AND senderid =2 ORDER BY created DESC'*/
            Cursor dbCcursor = db.rawQuery("SELECT * FROM "+TABLE_USER+" WHERE userid= '" + userid + "'", null);
            Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    userModel.setUid(dbCcursor.getInt(0));
                    userModel.setUserid(dbCcursor.getInt(1));
                    userModel.setFullname(dbCcursor.getString(2));
                    userModel.setRoleid(dbCcursor.getInt(3));
                    userModel.setAddress(dbCcursor.getString(4));
                    userModel.setMobile(dbCcursor.getString(5));
                    userModel.setToken(dbCcursor.getString(6));
                    userModel.setEmail(dbCcursor.getString(7));
                    userModel.setProfilepic(dbCcursor.getString(8));
                    userModel.setLatitute(dbCcursor.getDouble(9));
                    userModel.setLongitude(dbCcursor.getDouble(10));
                    userModel.setDevice(dbCcursor.getString(11));
                    userModel.setIsactive(dbCcursor.getInt(12));
                    userModel.setCreated(dbCcursor.getString(13));
                    userModel.setCreatedby(dbCcursor.getInt(14));
                    userModel.setModified(dbCcursor.getString(15));
                    userModel.setModifiedby(dbCcursor.getInt(16));

                } catch (Exception ex) {
                    Log.e(TAG, "GetOfflinePartner: ", ex);
                }
            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParentUser: ", ex);
        }
        Log.e("GetUser: ", userModel.toString());
        return userModel;
    }

    private byte[] getBlobImage(final String url){
        final byte[][] photoByte = new byte[1][1];

        new Thread(new Runnable() {
            public void run() {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    URL url1 = new URL(url);
                    Bitmap image = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                    image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] photo = baos.toByteArray();
                    photoByte[0] = photo;
                } catch (Exception e) {
                    Log.e("ImageManager", "Error: " + e.toString());
                }

            }
        }).start();

        return photoByte[0];
    }


    public long addUpdateUser(int userid, UserModel userModel){
        SQLiteDatabase db = databaseAccess.getWritableDatabase();
        long  result = 0;
        Log.e(TAG, "addUpdateUser: userid "+userid );
        Cursor c = db.rawQuery("SELECT * FROM users WHERE userid=?", new String[]{String.valueOf(userid)});
        Log.e(TAG, "ifUserExists: "+c.getCount() );
        if(c.getCount()>0)
        {
            // update
            result = updateData(userid,userModel) ;
            Log.e(TAG, "addUpdateUser: update "+result );
            return result;
        }
        else
        {
            //new entry
            result = saveUser(userModel) ;
            Log.e(TAG, "addUpdateUser: save "+result );
            return result;
        }
    }

}
