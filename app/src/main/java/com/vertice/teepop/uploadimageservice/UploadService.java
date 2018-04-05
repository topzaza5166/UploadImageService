package com.vertice.teepop.uploadimageservice;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

/**
 * Created by VerDev06 on 4/5/2018.
 */

public class UploadService extends IntentService {

    private static final int NOTIFICATION_ID_PROGRESS = 25;
    private static final int NOTIFICATION_ID_SUCCESSFUL = 26;
    private static final String NOTIFICATION_CHANNEL_PROGRESS = "upload_notification";
    private static final String NOTIFICATION_NAME_PROGRESS = "Upload";

    public UploadService() {
        super("");
    }

    public UploadService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        clearUploadSuccessful();
        prepareUploadProgress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showUploadSuccessful();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_PROGRESS,
                NOTIFICATION_NAME_PROGRESS,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableVibration(true);
        notificationChannel.enableLights(true);
        getNotificationManager().createNotificationChannel(notificationChannel);
    }

    private void prepareUploadProgress() {
        Notification notification = createProgressNotification("", "", 0);
        startForeground(NOTIFICATION_ID_PROGRESS, notification);
    }

    private void updateUploadProgress(int progress, String filePath) {
        Notification notification = createProgressNotification("", "", 0);
        startForeground(NOTIFICATION_ID_PROGRESS, notification);
    }

    private Notification createProgressNotification(String contentTitle, String contentText, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_PROGRESS);
        builder.setSubText("Upload Progressing");
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Upload Started");
        builder.setProgress(100, progress, false);
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }

    private void showUploadSuccessful() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_PROGRESS);
        builder.setContentTitle("Upload Complete");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Upload Complete");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        getNotificationManager().notify(NOTIFICATION_ID_SUCCESSFUL, builder.build());
    }

    private void clearUploadSuccessful() {
        getNotificationManager().cancel(NOTIFICATION_ID_SUCCESSFUL);
    }

    @SuppressLint("ServiceCast")
    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
