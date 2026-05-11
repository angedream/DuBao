package com.zilong.dubao;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
public class NotificationMsg {
    private String channelID="DUBAO";
    private String channelName="嘟宝安心守护";
    private NotificationManager manager=null;
    static int id=0;
    private void createNotificationChannel() {
        manager =(NotificationManager) app.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channel);
    }
    public void sendNotification(String title,String content) {
        if (manager==null){
            createNotificationChannel();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(app.getContext(), channelID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(content);
        manager.notify(id, builder.build());
        id++;
    }
    public void sendNotification(String title, String content, Intent intent) {
        if (manager==null){
            createNotificationChannel();
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(app.getContext(), id, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(app.getContext(), channelID)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(content).setPriority(NotificationCompat.PRIORITY_MAX);
        manager.notify(id, builder.build());
        id++;
    }
}
