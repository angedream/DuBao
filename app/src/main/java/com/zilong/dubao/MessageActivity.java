package com.zilong.dubao;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MessageActivity extends AppCompatActivity {
    String dumaName;
    String dumaId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        dumaName=getIntent().getStringExtra("dumaName");
        dumaId=getIntent().getStringExtra("dumaId");
        ((TextView)findViewById(R.id.name)).setText(dumaName);
        ((TextView)findViewById(R.id.dumaId)).setText(dumaId);
        initbtn();
    }
    private void initbtn(){
        Button agree=findViewById(R.id.agree);
        Button refuse=findViewById(R.id.refuse);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdlg();

            }
        });
        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void showdlg(){
        new AlertDialog.Builder(this)
                .setTitle("重要提示")          // 标题
                .setMessage("确定要执行此操作吗？")  // 消息内容
                .setIcon(R.drawable.logo)  // 图标（可选）
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bindDuMa();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    private void bindDuMa(){
        MyDB myDB=new MyDB("dubao.db",null,12);

        SQLiteDatabase db =myDB.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM duma where dumaId=?",new String[]{dumaId});
        if (cursor!=null&&cursor.moveToFirst()){
            do{
                // 已经绑定

                MyMqttClient.Msg msg=new MyMqttClient.Msg("binded",dumaName,dumaId,MyService.dubaoId);
                Gson gson = new Gson();
                String json = gson.toJson(msg);
                MyService.myMqttClient.publish("/duma/"+dumaId,json);
                cursor.close();
                db.close();
                return;
            }while (cursor.moveToNext());
        }

        String sql="";
        sql=String.format("INSERT INTO duma( \"dumaName\", \"dumaId\", \"bindDateTime\") VALUES ( '%s', '%s', '%d');",dumaName,dumaId ,System.currentTimeMillis());
        myDB.execSQL(sql);
        MyMqttClient.Msg msg=new MyMqttClient.Msg("bindok",dumaName,dumaId,MyService.dubaoId);
        Gson gson = new Gson();
        String json = gson.toJson(msg);
        MyService.myMqttClient.publish("/duma/"+dumaId,json);

    }
}