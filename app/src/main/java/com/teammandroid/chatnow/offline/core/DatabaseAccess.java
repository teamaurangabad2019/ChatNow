package com.teammandroid.chatnow.offline.core;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseAccess extends SQLiteOpenHelper{
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    public final static String DATABASE_NAME = "chatnowdb.db";
    private static final int DATABASE_VERSION = 2;

    public final static String TABLE_NOTIFICATION = "notification";
    public final static String TABLE_CHATTING = "chatting";
    public final static String TABLE_USER = "users";
    public final static String TABLE_GRP_NOTIFICATION = "grp_notification";

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */

    public DatabaseAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
       // this.openHelper = new DatabaseOpenHelper(context);
    }


    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_NOTIFICATION+
                " (messageid INTEGER PRIMARY KEY AUTOINCREMENT," + //1
                "firebaseid TEXT," +                           //2
                "receiverid INTEGER," +                           //3
                "senderid INTEGER," +                           //4
                "message TEXT," +                           //5
                "msgtime DATETIME," +                            //6
                "isread INTEGER," +                         //7
                "isdownloaded INTEGER," +                         //8
                "mediatype INTEGER," +                         //9
                "type INTEGER," +                         //10
                "replyToMsgId INTEGER," +                   //11
                "replyToMsgSenderId INTEGER," +           //12
                "replyToMsgText TEXT," +                   //13
                "replyToMsgTextMediaType INTEGER)");       //14


        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_GRP_NOTIFICATION+
                " (messageid INTEGER PRIMARY KEY AUTOINCREMENT," + //1
                "firebaseid TEXT," +                           //2
                "senderid INTEGER," +                           //4
                "message TEXT," +                           //5
                "msgtime DATETIME," +                            //6
                "isread INTEGER," +                         //7
                "mediatype INTEGER," +                         //9
                "gruoupid TEXT," +                         //9
                "type INTEGER)");       //14

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_CHATTING+
                " (chatid INTEGER PRIMARY KEY AUTOINCREMENT," + //1
                "senderid INTEGER," +                      //2
                "sendername TEXT," +                            //3
                "receiverid INTEGER," +                        //4
                "receivername TEXT," +                           //5
                "message TEXT," +//6
                "profilepic TEXT," +  //7
                "datetime TEXT)");                        //8

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_USER+
                " (uid INTEGER PRIMARY KEY AUTOINCREMENT," + //1
                "userid INTEGER ," + //2
                "fullname TEXT," + //3
                "roleid INTEGER," + //4
                "address TEXT," +   //5
                "mobile TEXT," +    //6
                "token TEXT," +     //7
                "email TEXT," +     //8
                "profilepic TEXT," +  //9
                "latitute DOUBLE," +//10
                "longitude DOUBLE," +//11
                "device TEXT," +//12
                "isactive INTEGER," +//13
                "created TEXT," + //14
                "createdby INTEGER," +//15
                "modified TEXT," + //16
                "modifiedby INTEGER)");                        //17

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATTING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
    }

    /**
     * Open the database connection.
     */
    public void open() {
        try {
            //this.database = openHelper.getWritableDatabase();
            this.database = this.getWritableDatabase();
        } catch (Exception ex) {
            Log.e("close", ex.getMessage());
        }
    }

    /**
     * Close the database connection.
     */
    public void close() {
        try {
            if (database != null) {
                if (this.database.isOpen())
                    this.database.close();
            }
        } catch (Exception ex) {
            Log.e("close", ex.getMessage());
        }
    }


    public boolean execSQL(String sqlCommand) throws Exception {
        try {
            open();
            this.database.execSQL(sqlCommand);
            return true;
        } catch (Exception ex) {
            Log.e("execute", ex.getMessage());
            throw ex;
        } finally {
            close();
        }
    }

    public Cursor rawQuery(String sqlCommand, String[] params) throws Exception {
        try {
            open();
            return this.database.rawQuery(sqlCommand, params);
        } catch (Exception ex) {
            Log.e("execute", ex.getMessage());
            throw ex;
        } finally {

        }
    }

    public long insert(String tableName, ContentValues contentValues) throws Exception {
        try {
            open();
            return this.database.insert(tableName, null, contentValues);
        } catch (Exception ex) {
            Log.e("execute", ex.getMessage());
            throw ex;
        } finally {
            close();
        }
    }

    /*
     * exeample
     * database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{contact.getID()});
     * database.update(TableName, cv, "_id="+id, null);
     * */
    public long update(String tableName, ContentValues values, String whereClause, String[] whereArgs) throws Exception {
        try {
            open();
            return this.database.update(tableName, values, whereClause, whereArgs);
        } catch (Exception ex) {
            Log.e("execute", ex.getMessage());
            throw ex;
        } finally {
            close();
        }
    }

    /*
     * example
     *
     * database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{contact.getID()});
     * */
    public int delete(String tableName, String whereClause, String[] whereArgs) throws Exception {
        try {
            open();
            return this.database.delete(tableName, whereClause, whereArgs);
        } catch (Exception ex) {
            Log.e("delete", ex.getMessage());
            throw ex;
        } finally {
            close();
        }
    }

    /*
     * example
     *
     * database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{contact.getID()});
     * */
    public long count(String tableName) throws Exception {
        try {
            open();
            Cursor cursor = this.database.rawQuery("SELECT COUNT (*) FROM " + tableName, null, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        } catch (Exception ex) {
            Log.e("count", ex.getMessage());
            throw ex;
        } finally {
            close();
        }
    }

    public long insertMultiple(String tableName, ArrayList<ContentValues> contentValues) {
        long respone = 0;

        try {
            open();
            this.database.beginTransaction();
            for (int i = 0; i < contentValues.size(); i++) {
                respone = this.database.insert(tableName, null, contentValues.get(i));
            }
            this.database.setTransactionSuccessful();
            this.database.endTransaction();
        } catch (Exception ex) {
            Log.e("execute", ex.getMessage());
            throw ex;
        } finally {
            close();
        }

        return  respone;
    }
}