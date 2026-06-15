package com.zilong.dubao;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class echo {
    static public void print(String s){

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        int line = stackTraceElement.getLineNumber();
        String fileName=stackTraceElement.getFileName();
        if(s!=null){
            Log.d("zilong:"+fileName+"[" + line +"]" , s);
        }
    }

    static void toast(String s){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(app.getContext(), s, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

    }
    static void toast(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(app.getContext(), "有故无殒,亦无殒也", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

    }
}
