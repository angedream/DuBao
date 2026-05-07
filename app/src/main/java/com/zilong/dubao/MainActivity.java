package com.zilong.dubao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    NotificationMsg notificationMsg=new NotificationMsg(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBtn();
//        startService();
        ImageView qrCodeImageView = findViewById(R.id.qrCode);
        uuid u=new uuid();
        String dubaoId=u.getuuid(this);
        Log.d("uuid",dubaoId);
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        int foreground = Color.parseColor("#2196F3"); // 前景蓝
        int background = Color.YELLOW; // 背景白
        Bitmap qrBitmap = QRCodeUtil.ShowQRCode(
                dubaoId, logoBitmap, 0.2f, foreground, background);
        qrCodeImageView.setImageBitmap(qrBitmap);
    }

    private void startService(){
        Intent i = new Intent(this, MyService.class);
        startForegroundService(i);
    }


    private void initBtn(){
        Button button =findViewById(R.id.sendmsg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("name","嘟妈101");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                notificationMsg.sendNotification("来自嘟妈的消息","快起床出去玩了",intent);
                parseJson();
            }
        });
    }


    class Msg{
        String code;
        String dumaName;
        String dumaId;
        String dubaoId;
    }
    private void parseJson(){
        String json="{\"code\":\"bind\",\"dumaName\":\"嘟妈\",\"dumaId\":\"f1122aeb-f2b0-400d-9919-eddd2eaebaa2\",\"dubaoId\":\"cfb20ccc-8c53-4434-85bb-a171c3ca7c0c\"}";
        Gson gson = new Gson();
        Msg msg = gson.fromJson(json, Msg.class);
        ((TextView)findViewById(R.id.code)).setText(msg.code);
        ((TextView)findViewById(R.id.dumaName)).setText(msg.dumaName);
        ((TextView)findViewById(R.id.dumaId)).setText(msg.dumaId);
        ((TextView)findViewById(R.id.dubaoId)).setText(msg.dubaoId);



    }





}