package com.zilong.dubao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyDB extends SQLiteOpenHelper {
    public MyDB(){
        super(app.getContext(), MyConfig.dbname, null, MyConfig.dbversion);


    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(app.getContext(),"数据库与表创建成功",Toast.LENGTH_SHORT).show();
        String dumaTabel="CREATE TABLE duma (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dumaName text,dumaId text,bindDateTime INTEGER)";
        String gps="CREATE TABLE gps (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,type INTEGER, lat REAL,lng REAL,acc REAl,addr TEXT,country TEXT,province TEXT,city TEXT,district TEXT,street TEXT,streetNum TEXT,cityCode TEXT,adCode TEXT,aoiName TEXT,buildingId TEXT,floor TEXT,accuracyStatus INTEGER,dtime INTEGER)";

        db.execSQL(gps);
        db.execSQL(dumaTabel);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        String fence="CREATE TABLE fence (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, label text,dumaId text,radius INTEGER,lng REAL,lat REAl,dtime INTEGER)";
//        db.execSQL("drop table fence");
//        db.execSQL(fence);

    }

    public boolean execSQL(String sql){
        try {
            SQLiteDatabase db =getWritableDatabase();
            db.execSQL(sql);

            db.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();

        }
        return  false;

    }
    public String getgps(String param){
        int page=1;
        int rows=20;
        long stop = System.currentTimeMillis();
        long start=stop-24*3600*1000;
        try {
            JSONObject jsonObject = new JSONObject(param);
            page = jsonObject.getInt("page");
            rows = jsonObject.getInt("rows");
            stop=jsonObject.getLong("stop");
            start=jsonObject.getLong("start");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int offset=(page-1)*rows;
        String sql=String.format("SELECT * FROM gps where dtime<%d and dtime>%d ORDER BY id DESC LIMIT %d,%d  ",stop,start,offset,rows);
        String s="";
        SQLiteDatabase db =getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor!=null&&cursor.moveToFirst()){
            do{
                s=cursorToJson(cursor);
            }while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return s;

    }
    private String cursorToJson(Cursor cursor) {
        JSONArray jsonArray = new JSONArray();

        if (cursor != null && cursor.moveToFirst()) {
            int columnCount = cursor.getColumnCount();

            do {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = cursor.getColumnName(i);
                    String value = cursor.getString(i);
                    try {
                        jsonObject.put(columnName, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());
        }

        return jsonArray.toString();
    }
}
