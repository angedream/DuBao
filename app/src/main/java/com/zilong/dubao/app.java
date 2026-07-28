package com.zilong.dubao;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;



public class app extends Application  {
    private  static Context context;
    private int activityCount = 0;
    private static boolean isInBackground = false;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                if (isInBackground) {
                    isInBackground = false;
                    System.out.println("App 进入前台");
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    isInBackground = true;
                    System.out.println("App 进入后台");
                }
            }

            // 其他空实现方法
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            @Override public void onActivityResumed(Activity activity) {}
            @Override public void onActivityPaused(Activity activity) {}
            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            @Override public void onActivityDestroyed(Activity activity) {}
        });


    }


    public static Context getContext(){
        return context;
    }

    public static boolean isAppInBackground() {
        return isInBackground;
    }



}
