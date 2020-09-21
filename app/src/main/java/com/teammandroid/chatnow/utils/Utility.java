package com.teammandroid.chatnow.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    private static final String TAG = Utility.class.getSimpleName();
    private static Float density = 1f;

    public static void launchActivity(Activity activity, Class<?> mClass, boolean shouldFinishParent, Bundle bundle) {
        Intent intent = new Intent(activity, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        if (shouldFinishParent) {
            activity.finish();
        }
    }

    public static void launchActivityForResult(Activity activity, Class<?> mClass, int requestCode) {
        Intent intent = new Intent(activity, mClass);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launchActivityWithContext(Context context, Class<?> mClass) {
        Intent intent = new Intent(context, mClass);
        context.startActivity(intent);
    }

    public static void launchActivityForResult(Activity activity, Class<?> mClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launchActivityForResultFromFragment(Fragment fragment, Activity activity, Class<?> mClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void launchActivity(Activity activity, Class<?> mClass, boolean shouldFinishParent) {
        Intent intent = new Intent(activity, mClass);
        activity.startActivity(intent);
        if (shouldFinishParent) {
            activity.finish();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void finishWithResult(Activity activity, Bundle bundle, int result) {
        Intent i = new Intent();
        if (bundle != null) {
            i.putExtras(bundle);
        }
        activity.setResult(result, i);
        activity.finish();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the cu rrently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view = null;
    }

    public static String getDeviceToken(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean emailValidate(String email) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean numberValidation(String number) {
        if (number.length()!=10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(number).matches();
        }
        //return Pattern.compile("^[0-9]+$").matcher(number).matches();
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static String getDeviceName() {
        return Build.MANUFACTURER + " - " + Build.MODEL;
    }

    public static void showErrorMessage(Activity activity, String msg) {
        try {
            Snackbar.make(activity.getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
        }
        catch (Exception ex){
            Log.e(TAG, "showErrorMessage: ", ex);
        }
    }

    public static void showErrorMessage(Activity activity, String msg, int length) {
        try {
            Snackbar.make(activity.getWindow().getDecorView().getRootView(), msg, length).show();
        }
        catch (Exception ex){
            Log.e(TAG, "showErrorMessage: ", ex);
        }
    }

    public static String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-4:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("KK:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT-4:00"));
        String localTime = date.format(currentLocalTime);
        return localTime;
    }


//    public static void sendIntent(Context context, String action) {
//        final Intent intent = new Intent(context, MusicService.class);
//        intent.setAction(action);
//        context.startService(intent);
//    }
//
//    public static void sendIntent(Context context, String action, Bundle b) {
//        final Intent intent = new Intent(context, MusicService.class);
//        intent.putExtras(b);
//        intent.setAction(action);
//        context.startService(intent);
//    }


    public static boolean containsURL(String content){
        String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(REGEX,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        if(m.find()) {
            return true;
        }

      /*  String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(content);//replace with string to compare
        if (m.find()){
            return true;
        }*/
        return false;
    }


    public static int dp(Float value) {
        if (density == 1f) {
            checkDisplaySize();
        }
         if (value == 0f) {
          return   0;
        } else {
            return (int) Math.ceil((density * value));
         }
    }


    private static void checkDisplaySize() {
        try {
            //density = context.resources.displayMetrics.density;
            density = Resources.getSystem().getDisplayMetrics().density;
        } catch ( Exception  e) {
            Log.e(TAG, "checkDisplaySize: e "+e.getMessage() );
        }

    }

    public static String getFileExtension(String filename){
        String extension = "";

        int i = filename.lastIndexOf('.');
        int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

        if (i > p) {
            extension = filename.substring(i+1);
        }
        return extension;
    }


}
