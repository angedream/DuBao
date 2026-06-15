package com.zilong.dubao;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    public static MyMqttClient myMqttClient;
    public static String dubaoId;
    public static GpsGaode gpsGaode;
    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        uuid u=new uuid();
        dubaoId= u.getuuid();
        myMqttClient=new MyMqttClient(dubaoId);
        myMqttClient.connect();
        gpsGaode=new GpsGaode();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {

        myMqttClient.colse();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                "DUBAO",
                "嘟宝安心守护",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification= new NotificationCompat.Builder(this, "DUBAO")
                .setContentTitle("嘟宝")
                .setContentText("嘟宝安心守护孩子安全...")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentIntent(pendingIntent)
                .setOngoing(true)  // 不可滑动删除
                .build();
        startForeground(10001, notification);
    }
}