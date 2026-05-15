package com.zilong.dubao;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MyDB myDB=new MyDB("dubao.db",null,12);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBtn();
        startService();
        ImageView qrCodeImageView = findViewById(R.id.qrCode);
        uuid u=new uuid();
        String dubaoId=u.getuuid();
        Log.d("uuid",dubaoId);
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        int foreground = Color.parseColor("#2196F3"); // 前景蓝
        int background = Color.WHITE; // 背景白
        Bitmap qrBitmap = QRCodeUtil.ShowQRCode(
                dubaoId, logoBitmap, 0.2f, foreground, background);
        qrCodeImageView.setImageBitmap(qrBitmap);
    }
    private void initBtn(){
        Button insertBtn=findViewById(R.id.insertDB);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDB.getWritableDatabase();
                db.beginTransaction();
                try {
                    // 执行多个数据库操作
                    for (int i = 0; i < 3; i++) {
                        String sql="";
                        sql=String.format("INSERT INTO duma( \"dumaName\", \"dumaId\", \"bindDateTime\") VALUES ( '%s', '%s', '%d');","嘟妈"+i,"f1122aeb-f2b0-400d-9919-eddd2eaebaa2" ,System.currentTimeMillis());
                        Log.d("mqtt",sql);
                        db.execSQL(sql);
                    }
                    db.setTransactionSuccessful();

                } catch (Exception e) {
                    // 发生异常，自动回滚
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"发生异常，自动回滚",Toast.LENGTH_SHORT).show();
                }finally {
                    db.endTransaction();
                    db.close();
                }

            }
        });
        Button rollupBtn=findViewById(R.id.rollupDB);
        rollupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDB.getWritableDatabase();
                db.beginTransaction();
                try {
                    // 执行多个数据库操作
                    for (int i = 0; i < 3; i++) {
                        String sql="";
                        sql=String.format("INSERT INTO duma( \"dumaName\", \"dumaId\", \"bindDateTime\") VALUES ( '%s', '%s', '%d');","嘟妈"+i,"f1122aeb-f2b0-400d-9919-eddd2eaebaa2" ,System.currentTimeMillis());
                        db.execSQL(sql);
                        if (i==1){
                            throw  new Exception("失败");
                        }
                    }
                    db.setTransactionSuccessful();


                } catch (Exception e) {
                    // 发生异常，自动回滚
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"发生异常，自动回滚",Toast.LENGTH_SHORT).show();
                }finally {
                    db.endTransaction();
                    db.close();
                }

            }
        });

        Button queryBtn=findViewById(R.id.queryDB);
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db =myDB.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM duma",null);
                if (cursor!=null&&cursor.moveToFirst()){
                    do{
                        @SuppressLint("Range") String dumaName=cursor.getString(cursor.getColumnIndex("dumaName"));
                        @SuppressLint("Range") String dumaId=cursor.getString(cursor.getColumnIndex("dumaId"));
                        @SuppressLint("Range") int bindDateTime=cursor.getInt(cursor.getColumnIndex("bindDateTime"));
                        Toast.makeText(MainActivity.this,dumaName,Toast.LENGTH_SHORT).show();

                    }while (cursor.moveToNext());
                    cursor.close();
                    db.close();

                }


            }

        });
        Button delBtn=findViewById(R.id.delDB);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql="";
                sql=String.format("DELETE FROM duma","f1122aeb-f2b0-400d-9919-eddd2eaebaa2");
                myDB.execSQL(sql);

            }
        });


    }

    private void startService(){
        Intent i = new Intent(this, MyService.class);
        startForegroundService(i);
    }


}