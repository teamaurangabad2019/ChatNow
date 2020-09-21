package com.teammandroid.chatnow.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.activities.ChattingActivity;
import com.teammandroid.chatnow.activities.HomeActivity;
import com.teammandroid.chatnow.activities.firebase.GroupChattingActivity;
import com.teammandroid.chatnow.models.GrpNotificationModel;
import com.teammandroid.chatnow.offline.GrpNotificationDatabaseHelper;
import com.teammandroid.chatnow.offline.NotificationDatabaseHelper;
import com.teammandroid.chatnow.models.NotificationModel;
import com.teammandroid.chatnow.models.UserModel;
import com.teammandroid.chatnow.receiver.CancelNotificationReceiver;
import com.teammandroid.chatnow.utils.SessionManager;
import com.teammandroid.chatnow.utils.Utility;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.teammandroid.chatnow.Config.Config.NOTIFICATION_ID;

public class ChatNowFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "ChatNowMSGING";
    private static final String GROUP_KEY = "com.teammandroid.chatnow.GROUP_KEY";
    ValueEventListener seenListener;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "ChkToken: " + s);
        SessionManager.saveFirebaseTokens(this, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived: " + remoteMessage);
        super.onMessageReceived(remoteMessage);
        notifyUser(this, remoteMessage);
    }

    public void notifyUser(ChatNowFirebaseMessagingService activity, RemoteMessage remoteMessage) {

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        //  Log.e("notification from", remoteMessage.getFrom().toString());
        Log.e(TAG, "notifyUser: from " + remoteMessage.getFrom().toString());

        SessionManager sessionManager = new SessionManager(activity.getApplicationContext());
        UserModel user = sessionManager.getUserDetails();
        String msg = remoteMessage.getData().get("message");
        Date currentTime = Calendar.getInstance().getTime();
        String sdate = DateFormat.getDateTimeInstance().format(currentTime);

        if (Integer.parseInt(remoteMessage.getData().get("isFromGroup")) == 1){
            GrpNotificationModel notification = new GrpNotificationModel();
            notification.setMessageid(0);
            notification.setFirebaseid(remoteMessage.getData().get("msgUid"));//serverid
            notification.setSenderid(Integer.parseInt(remoteMessage.getData().get("senderid")));
            notification.setMessage(msg);
            notification.setMsgtime(remoteMessage.getData().get("created"));
            notification.setIsread(0);
            notification.setMediatype(Integer.parseInt(remoteMessage.getData().get("mediatype")));
            notification.setGruoupid(remoteMessage.getData().get("groupId"));
            notification.setType(2);//2 for incoming notifications

            String msgUid = remoteMessage.getData().get("msgUid");
            saveGrpNotificationToSqlite(notification, activity);
           // updateGrpFirebaseStatus(msgUid, notification, activity);

            Intent notificationIntent = new Intent();
            notificationIntent = new Intent(
                    activity.getApplicationContext(), GroupChattingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("groupId",remoteMessage.getData().get("groupId"));
           // bundle.putInt("partnerId", Integer.parseInt(remoteMessage.getData().get("senderid")));
            notificationIntent.putExtras(bundle);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
            stackBuilder.addParentStack(HomeActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(activity.getApplicationContext(), "notify_001");

            mBuilder.setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("message"))
                    .setDefaults(
                            Notification.DEFAULT_SOUND
                                    | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setGroup(GROUP_KEY)
                    .setGroupSummary(true)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel title",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.canShowBadge();
                channel.setShowBadge(true);
                channel.setLightColor(Color.CYAN);
                notificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            if (notificationManager != null) {
                notificationManager.notify(createRandomCode(7), mBuilder.build());
            }
        }
        else {
        NotificationModel notification = new NotificationModel();
        notification.setMessageid(0);
        notification.setFirebaseid(remoteMessage.getData().get("msgUid"));//serverid
        notification.setReceiverid(user.getUserid());
        notification.setSenderid(Integer.parseInt(remoteMessage.getData().get("senderid")));
        notification.setMessage(msg);
        notification.setMsgtime(remoteMessage.getData().get("created"));
        notification.setIsread(0);
        notification.setIsdownloaded(0);
        notification.setMediatype(Integer.parseInt(remoteMessage.getData().get("mediatype")));
        notification.setType(2);//2 for incoming notifications
        notification.setReplyToMsgId(0);
        notification.setReplyToMsgSenderId(Integer.parseInt(remoteMessage.getData().get("replyToMsgSenderId")));
        notification.setReplyToMsgText(remoteMessage.getData().get("replyToMsgText"));
        notification.setReplyToMsgTextMediaType(Integer.parseInt(remoteMessage.getData().get("replyToMsgTextMediaType")));
        Log.e(TAG, "notifyUser: id" + remoteMessage.getData().get("id"));

            String msgUid = remoteMessage.getData().get("msgUid");
            saveNotificationToSqlite(notification, activity);
            updateFirebaseStatus(msgUid, notification, activity);

            Intent notificationIntent = new Intent();
            notificationIntent = new Intent(
                    activity.getApplicationContext(), ChattingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("partnerId", Integer.parseInt(remoteMessage.getData().get("senderid")));
            notificationIntent.putExtras(bundle);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
            stackBuilder.addParentStack(HomeActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(activity.getApplicationContext(), "notify_001");

            mBuilder.setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("message"))
                    .setDefaults(
                            Notification.DEFAULT_SOUND
                                    | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setGroup(GROUP_KEY)
                    .setGroupSummary(true)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel title",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.canShowBadge();
                channel.setShowBadge(true);
                channel.setLightColor(Color.CYAN);
                notificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            if (notificationManager != null) {
                notificationManager.notify(createRandomCode(7), mBuilder.build());
            }
        }
           }

    private void saveGrpNotificationToSqlite(GrpNotificationModel notification, ChatNowFirebaseMessagingService activity) {
        GrpNotificationDatabaseHelper notificationSqliteOperations = new GrpNotificationDatabaseHelper(activity.getApplicationContext());
        long result = notificationSqliteOperations.saveNotification(notification);
        //Log.e("insertResult",""+result);
        Log.e(TAG, "saveNotificationToSqlite: " + result);
    }

    private void updateGrpFirebaseStatus(String msgUid, GrpNotificationModel notification, ChatNowFirebaseMessagingService activity) {
        DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Chats");
        dref.keepSynced(true);
        //dref.child(model.getMessageid()).child("contacts")
        dref.orderByChild("messageid").equalTo(msgUid).limitToFirst(10);
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //  FirebaseChatModel chatModel=snapshot.getValue(FirebaseChatModel.class);
                    //   if (chatModel.getReceiver().equals(firebaseUser.getUid()) && chatModel.getSender().equals(userId)){
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("msgStatus","delivered");
                    snapshot.getRef().updateChildren(hashMap);
                    // }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // dref.removeEventListener(seenListener);

    }
        private void updateFirebaseStatus(String msgUid, NotificationModel notification, ChatNowFirebaseMessagingService activity) {
        DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Chats");
        dref.keepSynced(true);
        //dref.child(model.getMessageid()).child("contacts")
        dref.orderByChild("messageid").equalTo(msgUid).limitToFirst(10);
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //  FirebaseChatModel chatModel=snapshot.getValue(FirebaseChatModel.class);
                    //   if (chatModel.getReceiver().equals(firebaseUser.getUid()) && chatModel.getSender().equals(userId)){
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("msgStatus","delivered");
                    snapshot.getRef().updateChildren(hashMap);
                    // }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       // dref.removeEventListener(seenListener);

    }

    public int createRandomCode(int codeLength) {
        char[] chars = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return Integer.parseInt(sb.toString());
    }

    private static void saveNotificationToSqlite(NotificationModel notification, FirebaseMessagingService activity) {
        NotificationDatabaseHelper notificationSqliteOperations = new NotificationDatabaseHelper(activity.getApplicationContext());
        long result = notificationSqliteOperations.saveNotification(notification);
        //Log.e("insertResult",""+result);
        Log.e(TAG, "saveNotificationToSqlite: " + result);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void sendNotification(String messageBody, Intent intent) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Intent onCancelNotificationReceiver = new Intent(this, CancelNotificationReceiver.class);
        PendingIntent onCancelNotificationReceiverPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0,
                onCancelNotificationReceiver, 0);
        String notificationHeader = this.getResources().getString(R.string.app_name);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = manager.getActiveNotifications();

        for (int i = 0; i < notifications.length; i++) {
            if (notifications[i].getPackageName().equals(getApplicationContext().getPackageName())) {
                Log.d("Notification", notifications[i].toString());
                Intent startNotificationActivity = new Intent(this, ChattingActivity.class);
                startNotificationActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startNotificationActivity,
                        PendingIntent.FLAG_ONE_SHOT);
                Notification notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(notificationHeader)
                        .setContentText("Tap to open")
                        .setAutoCancel(true)
                        .setStyle(getStyleForNotification(messageBody))
                        .setGroupSummary(true)
                        .setGroup(GROUP_KEY)
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(onCancelNotificationReceiverPendingIntent)
                        .build();
                SharedPreferences sharedPreferences = getSharedPreferences("NotificationData", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(String.valueOf(new Random(NOTIFICATION_ID)), messageBody);
                editor.apply();
                notificationManager.notify(NOTIFICATION_ID, notification);
                return;
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(notificationHeader)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(onCancelNotificationReceiverPendingIntent)
                .build();
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationData", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(new Random(NOTIFICATION_ID)), messageBody);
        editor.apply();
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder);
    }

    private NotificationCompat.InboxStyle getStyleForNotification(String messageBody) {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        SharedPreferences sharedPref = getSharedPreferences("NotificationData", 0);
        Map<String, String> notificationMessages = (Map<String, String>) sharedPref.getAll();
        Map<String, String> myNewHashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : notificationMessages.entrySet()) {
            myNewHashMap.put(entry.getKey(), entry.getValue());
        }
        inbox.addLine(messageBody);
        for (Map.Entry<String, String> message : myNewHashMap.entrySet()) {
            inbox.addLine(message.getValue());
        }
        inbox.setBigContentTitle(this.getResources().getString(R.string.app_name))
                .setSummaryText("Tap to open");
        return inbox;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       /* Intent notificationIntent = new Intent();
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        UserModel user = PrefManager.getUserFromSharedPref(this.getApplicationContext());

        switch (user.getUserId()) {
            case 1:
                notificationIntent = new Intent(
                        this.getApplicationContext(), CustomerChatActivity.class);
                startActivity(notificationIntent);
                break;

            case 2:
                notificationIntent = new Intent(
                        this.getApplicationContext(), TrainersDashboardActivity.class);
                startActivity(notificationIntent);
                break;

            case 3:
                notificationIntent = new Intent(
                        this.getApplicationContext(), ChattingListActivity.class);
                startActivity(notificationIntent);
                break;

        }*/

    }

}
