package com.zilong.dubao;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.UUID;

public class uuid {
    private String createUUID(){
        String s="";
        s=UUID.randomUUID().toString();
        return s;

    }
    public String getuuid(Context context){
        SharedPreferences  preferences=context.getSharedPreferences("uuid",MODE_PRIVATE);
        String uuid= preferences.getString("id","");
        if (uuid.equals("")){
            uuid=createUUID();
            SharedPreferences.Editor editor=context.getSharedPreferences("uuid",MODE_PRIVATE).edit();
            editor.putString("id",uuid);
            editor.apply();
            return uuid;
        }
        return uuid;
    }
}
