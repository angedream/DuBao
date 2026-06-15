package com.zilong.dubao;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class app extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
//        Toast.makeText(context,"全局获取Context",Toast.LENGTH_SHORT).show();
    }
    public static Context getContext(){
        return context;
    }
}
