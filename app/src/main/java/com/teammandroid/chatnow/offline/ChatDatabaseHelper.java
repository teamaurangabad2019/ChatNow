

package com.teammandroid.chatnow.offline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.teammandroid.chatnow.models.MyChatModel;
import com.teammandroid.chatnow.offline.core.DatabaseAccess;

import java.util.ArrayList;

public class ChatDatabaseHelper {

    private static final String TAG = ChatDatabaseHelper.class.getSimpleName();

    public final static String DATABASE_NAME = "chatnowdb.db";

    public final static String TABLE_CHATTING = "chatting";

    public final static String COL_1 = "chatid";
    public final static String COL_2 = "senderid";
    public final static String COL_3 = "sendername";
    public final static String COL_4 = "receiverid";
    public final static String COL_5 = "receivername";
    public final static String COL_6 = "message";
    public final static String COL_7 = "profilepic";
    public final static String COL_8 = "datetime";

    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase database;
    private static DatabaseAccess databaseAccess;

    Context context;
    public ChatDatabaseHelper(@Nullable Context context) {
       // super(context, DATABASE_NAME, null, DATABASE_VERSION);

        try {
            this.databaseAccess = new DatabaseAccess(context);
            // super(context, DATABASE_NAME, null, DATABASE_VERSION);
        } catch (Exception ex) {
            Log.e(TAG, "ChatSqliteOperations: ", ex);
        }
    }

   /* @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_CHATTING+
                " (chatid INTEGER PRIMARY KEY AUTOINCREMENT," + //1
                "senderid INTEGER," +                      //2
                "sendername TEXT," +                            //3
                "receiverid INTEGER," +                        //4
                "receivername TEXT," +                           //5
                "message TEXT," +                        //6
                "datetime TEXT)");                        //8

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATTING);

    }
*/
    //to insert data inside service booking table
    public boolean insertData(int senderid,String sendername,int receiverid,String receivername,String message,String profilePic,String datetime)
    {

        SQLiteDatabase db = databaseAccess.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2,senderid);
        contentValues.put(COL_3,sendername);
        contentValues.put(COL_4,receiverid);
        contentValues.put(COL_5,receivername);
        contentValues.put(COL_6,message);
        contentValues.put(COL_7,profilePic);
        contentValues.put(COL_8,datetime);

        //long result = db.insert(TABLE_NEW_CUSTOMER, null, contentValues);

        long result = db.insert(TABLE_CHATTING, null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }

    }

    public boolean isChattingAvailable(int senderId, int receiverId) {

        // array of columns to fetch
        String[] columns = {
                COL_1
        };
        SQLiteDatabase db = databaseAccess.getReadableDatabase();
        // selection criteria

        //String selection = COL_10 + " = ?" + " AND " + COL_11 + " = ?";

        String selection = COL_2 + " = ?" + " AND " + COL_4 + " = ?";


        // selection arguments
        String[] selectionArgs = {Integer.toString(senderId),Integer.toString(receiverId)};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_CHATTING, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public Integer updateChattingData(int senderId,int receiverId,String messaage,String profilepic,String msgDate){

        SQLiteDatabase db = databaseAccess.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, senderId);
        contentValues.put(COL_4,receiverId);
        contentValues.put(COL_6,messaage);
        contentValues.put(COL_7,profilepic);
        contentValues.put(COL_8,msgDate);
        /*return db.update(TABLE_CHATTING, contentValues,COL_2+"=? AND" +COL_4 + "=?",
                new String[]{String.valueOf(senderId),String.valueOf(receiverId)});*/
        String WHERE =  "senderid='"+senderId+"' AND receiverid='"+receiverId+"'  ";
        return db.update(TABLE_CHATTING, contentValues,WHERE,null);
    }

    public SQLiteDatabase getDatabase(){
        return databaseAccess.getWritableDatabase();
    }


     public long addUpdateChat(int userid, MyChatModel chatModel){
         SQLiteDatabase db = databaseAccess.getWritableDatabase();
         long  result = 0;
         Log.e(TAG, "addUpdateUser: userid "+userid );
         Cursor c = db.rawQuery("SELECT * FROM chatting WHERE senderid=? OR receiverid=?", new String[]{String.valueOf(userid),String.valueOf(userid)});
         Log.e(TAG, "ifUserExists: "+c.getCount() );
         if(c.getCount()>0)
         {
             // update
             result = updateData(userid,chatModel) ;
             Log.e(TAG, "addUpdateChat: update "+result );
             return result;
         }
         else
         {
             //new entry
             result = saveUser(chatModel) ;
             Log.e(TAG, "addUpdateChat: save "+result );
             return result;
         }
     }

     private long saveUser(MyChatModel chatModel) {
         SQLiteDatabase db = databaseAccess.getWritableDatabase();
         Log.e(TAG, "saveNotification: ");

         long response = 0;
         try {
             ContentValues contentValues = new ContentValues();
             contentValues.put(COL_2, chatModel.getSenderid());
            // contentValues.put(COL_3, chatModel.getSenderName());
            // contentValues.put(COL_4, chatModel.getReceiverid());
            // contentValues.put(COL_5, chatModel.getReceivername());
             contentValues.put(COL_6, chatModel.getMessage());
             contentValues.put(COL_7, chatModel.getProfilepic());
            // contentValues.put(COL_8, chatModel.getCreated());
             response = db.insert(TABLE_CHATTING, null, contentValues);

             Log.e(TAG, "saveUser: response " + response);
             //}
         } catch (Exception ex) {
             Log.e(TAG, "saveQuizStatus: ", ex);
         } finally {
             db.close();
         }
         return response;
     }

     private long updateData(int id, MyChatModel chatModel) {
         int result = 0;
         SQLiteDatabase db = databaseAccess.getWritableDatabase();
         ContentValues contentValues = new ContentValues();
         contentValues.put(COL_2, chatModel.getSenderid());
       /*  contentValues.put(COL_3, chatModel.getSenderName());
         contentValues.put(COL_4, chatModel.getReceiverid());
         contentValues.put(COL_5, chatModel.getReceivername());*/
         contentValues.put(COL_6, chatModel.getMessage());
         contentValues.put(COL_7, chatModel.getProfilepic());
        // contentValues.put(COL_8, chatModel.getCreated());

         String WHERE =  "senderid='"+chatModel.getSenderid()+"' AND receiverid='"+chatModel.getPartnerid()+"'  ";


         //String WHERE = "senderid='" + id + "OR receiverid='"+id+"'";
         return db.update(TABLE_CHATTING, contentValues, WHERE, null);

     }

    public ArrayList<MyChatModel> GetUserid() {
        Log.e(TAG, "GetUser: ");
        SQLiteDatabase db = databaseAccess.getWritableDatabase();

        ArrayList<MyChatModel> chatList = new ArrayList<>();

        try {
            Cursor dbCcursor = db.rawQuery("select * from " + TABLE_CHATTING, null);
            Log.e("dbCcursor: ", dbCcursor.toString());

            if (dbCcursor.moveToFirst()) do {
                try {
                    MyChatModel chatModel = new MyChatModel();
                  //  chatModel.setChattingid(dbCcursor.getInt(0));
                   // chatModel.setSenderid(dbCcursor.getInt(1));
                   // chatModel.setSenderName(dbCcursor.getString(2));
                  //  chatModel.setReceiverid(dbCcursor.getInt(3));
                   // chatModel.setReceivername(dbCcursor.getString(4));
                    chatModel.setMessage(dbCcursor.getString(5));
                    chatModel.setProfilepic(dbCcursor.getString(6));
                 //   chatModel.setCreated(dbCcursor.getString(7));

                    chatList.add((chatModel));
                    Log.e("GetRecentChats: ", chatList.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "GetAllUser: ", ex);
                }
            } while (dbCcursor.moveToNext());
        } catch (Exception ex) {
            Log.e(TAG, "GetParentNotifi: ", ex);
        }
        return chatList;
    }

}
