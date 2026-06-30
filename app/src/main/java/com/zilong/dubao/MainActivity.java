package com.zilong.dubao;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.apis.utils.core.api.AMapUtilCoreApi;

public class MainActivity extends AppCompatActivity {
    private MyDB myDB=new MyDB();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int aa=1002;
        if (aa==100){
            Intent intent = new Intent(this, DuMaActivity.class);
            startActivity(intent);
            return;
        }
        initBtn();
//        initGaoDeGPS();
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
                Cursor cursor = db.rawQuery("SELECT * FROM gps",null);
                if (cursor!=null&&cursor.moveToFirst()){
                    do{
                        @SuppressLint("Range") String dumaName=cursor.getString(cursor.getColumnIndex("city"));
                        @SuppressLint("Range") String dumaId=cursor.getString(cursor.getColumnIndex("province"));
                        @SuppressLint("Range") int bindDateTime=cursor.getInt(cursor.getColumnIndex("addr"));
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
                sql=String.format("DELETE FROM gps","f1122aeb-f2b0-400d-9919-eddd2eaebaa2");
                myDB.execSQL(sql);
            }
        });
    }

    private void startService(){
        Intent i = new Intent(this, MyService.class);
        startForegroundService(i);
        startprojectionManager();
    }

    public void startprojectionManager(){

        @SuppressLint({"NewApi", "LocalSuppress"}) MediaProjectionManager projectionManager = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        @SuppressLint({"NewApi", "LocalSuppress"}) Intent intent = projectionManager.createScreenCaptureIntent();
//        startActivityForResult(intent,PROJECTION_REQUEST_CODE);


        ActivityResultLauncher<Intent> screenCaptureLauncher;
        screenCaptureLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                // 这里可以使用 MediaProjection API 来捕获和共享屏幕
//                PeerConnectionUtils.i=data;
//                PeerConnectionUtils.resultcode=result.getResultCode();
                MyWebRtc.i=data;
                Toast.makeText(MainActivity.this,"tongyi........",Toast.LENGTH_SHORT).show();


            }
            else {
            }
        });
        screenCaptureLauncher.launch(intent);


    }
}