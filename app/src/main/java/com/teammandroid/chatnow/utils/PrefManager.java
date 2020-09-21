package com.teammandroid.chatnow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    public static int PRIVATE_MODE = 0;
    // Shared preferences file name
    public static final String PREF_NAME = "Dairy";
    // All Shared Preferences Keys
    private static final String USER_ID = "u_id";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    //
    public void setUSER_ID(int uid) {
        editor.putInt(USER_ID, uid);
        editor.commit();
    }

    public int getUSER_ID() {
        return pref.getInt(USER_ID, 0);
    }


}