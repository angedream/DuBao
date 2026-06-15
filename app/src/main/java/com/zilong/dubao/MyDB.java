package com.zilong.dubao;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDB extends SQLiteOpenHelper {
    public MyDB(){
        super(app.getContext(), MyConfig.dbname, null, MyConfig.dbversion);


    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(app.getContext(),"数据库与表创建成功",Toast.LENGTH_SHORT).show();
        String dumaTabel="CREATE TABLE duma (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dumaName text,dumaId text,bindDateTime INTEGER)";
        String gps="CREATE TABLE gps (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,type INTEGER, lat REAL,LNG REAL,acc REAl,addr TEXT,country TEXT,province TEXT,city TEXT,district TEXT,street TEXT,streetNum TEXT,cityCode TEXT,adCode TEXT,aoiName TEXT,buildingId TEXT,floor TEXT,accuracyStatus INTEGER,dtime INTEGER)";
        db.execSQL(gps);
        db.execSQL(dumaTabel);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE gps");
        String gps="CREATE TABLE gps (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,type INTEGER, lat REAL,LNG REAL,acc REAl,addr TEXT,country TEXT,province TEXT,city TEXT,district TEXT,street TEXT,streetNum TEXT,cityCode TEXT,adCode TEXT,aoiName TEXT,buildingId TEXT,floor TEXT,accuracyStatus INTEGER,dtime INTEGER)";
        db.execSQL(gps);


    }

    public boolean execSQL(String sql){
        try {
            SQLiteDatabase db =getWritableDatabase();
            db.execSQL(sql);
            db.close();
        }catch (SQLException e){
            e.printStackTrace();

        }
        return  false;

    }
}
