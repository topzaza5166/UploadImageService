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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    private List<byte[]> imageList = new ArrayList<>();

    private int maxSize = 1;

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            byte[] image = intent.getByteArrayExtra("image");
            imageList.add(image);
            maxSize++;
            updateUploadProgress();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            byte[] image = intent.getByteArrayExtra("image");
            upload(image);
        }
    }

    private void upload(byte[] image) {
        getUploadSingle(image)
                .subscribe(jsonResponse -> {
                    imageList.remove(image);
                    updateUploadProgress();
                }, Throwable::printStackTrace);
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
        Notification notification = createProgressNotification(0);
        startForeground(NOTIFICATION_ID_PROGRESS, notification);
    }

    private void updateUploadProgress() {
        int progress = (maxSize - imageList.size()) / maxSize * 100;
        Notification notification = createProgressNotification(progress);
        getNotificationManager().notify(NOTIFICATION_ID_PROGRESS, notification);
    }

    private Notification createProgressNotification(int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_PROGRESS);
        builder.setContentTitle("Upload in progress");
        builder.setContentText("Number of Image" + maxSize);
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

    private Single<JsonResponse> getUploadSingle(byte[] image) {
        return HttpManager.getInstance().getApiService()
                .upload(
                        createPartFromString("1qaz2wsx3edc4rfv"),
                        createPartFromString("satei_transaction"),
                        createPartFromString("upload_image"),
                        createPartFromString("{\"staff_code\" : \"00001\",\"kyoten_code\" : \"00001\",\"group_code\" : \"99\",\"syaten_code\" : \"00001\"}"),
                        createPartFromString("00001000010000000010"),
                        createPartFromString("1"),
                        createPartFromString(String.valueOf(maxSize)),
                        getPartFromByte(image)
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public MultipartBody.Part getPartFromByte(byte[] image) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        image
                );
        return MultipartBody.Part.createFormData("image", "filename", requestFile);
    }

    private RequestBody createPartFromString(String s) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, s);
    }
}
