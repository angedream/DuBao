package com.zilong.dubao;


import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.apis.utils.core.api.AMapUtilCoreApi;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GpsGaode {
    private Handler mWorkHandler;

    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            updateShow(amapLocation,null);
        }
    };
    Context context;
    public AMapLocationClient mLocationClient = null;
    GpsGaode(){
        this.context=app.getContext();
        HandlerThread handlerThread = new HandlerThread("worker");
        handlerThread.start();
        mWorkHandler = new Handler(handlerThread.getLooper());
        init();
    }
    private void init() {
        mWorkHandler.post(()->{
            //初始化定位
            try {
                AMapLocationClient.updatePrivacyShow(context,true,true);
                AMapLocationClient.updatePrivacyAgree(context,true);
                AMapUtilCoreApi.setCollectInfoEnable(false);

                //参数设置

                AMapLocationClientOption option = new AMapLocationClientOption();
                option.setOnceLocation(false);
                option.setInterval(1000*60);//一分钟
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                mLocationClient = new AMapLocationClient(app.getContext());
                mLocationClient.setLocationOption(option);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //设置定位回调监听
            mLocationClient.setLocationListener(this.mLocationListener);

            //启动定位
            mLocationClient.startLocation();
        });

    }
    public void onceLocation(MyMqttClient.Msg msg) {
        mWorkHandler.post(()->{
            //初始化定位
            try {
                AMapLocationClient.updatePrivacyShow(context,true,true);
                AMapLocationClient.updatePrivacyAgree(context,true);
                AMapUtilCoreApi.setCollectInfoEnable(false);

                //参数设置

                AMapLocationClientOption option = new AMapLocationClientOption();
                option.setOnceLocation(true);
                // 定位模式：
                //- Hight_Accuracy：高精度（GPS + 网络）
                //- Battery_Saving：省电（仅网络）
                //- Device_Sensors：仅设备（仅 GPS）
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);


                mLocationClient = new AMapLocationClient(app.getContext());
                mLocationClient.setLocationOption(option);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //设置定位回调监听
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    updateShow(aMapLocation,msg);
                }
            });

            //启动定位
            mLocationClient.startLocation();
        });

    }


    private void updateShow(AMapLocation amapLocation,MyMqttClient.Msg msg) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //解析定位结果
                int type=amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                double lat=amapLocation.getLatitude();//获取纬度
                double lng=amapLocation.getLongitude();//获取经度
                float acc=amapLocation.getAccuracy();//获取精度信息
                String addr =amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                String country=amapLocation.getCountry();//国家信息
                String province=amapLocation.getProvince();//省信息
                String city=amapLocation.getCity();//城市信息
                String district=amapLocation.getDistrict();//城区信息
                String street=amapLocation.getStreet();//街道信息
                String streetNum=amapLocation.getStreetNum();//街道门牌号信息
                String cityCode=amapLocation.getCityCode();//城市编码
                String adCode=amapLocation.getAdCode();//地区编码
                String aoiName=amapLocation.getAoiName();//获取当前定位点的AOI信息
                String buildingId=amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                String floor=amapLocation.getFloor();//获取当前室内定位的楼层
                int accuracyStatus=amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                long dtime=amapLocation.getTime();
                ContentValues values = new ContentValues();
                values.put("type",type);
                values.put("lat",lat);
                values.put("lng",lng);
                values.put("acc",acc);
                values.put("addr",addr);
                values.put("country",country);
                values.put("province",province);
                values.put("city",city);
                values.put("district",district);
                values.put("street",street);
                values.put("streetNum",streetNum);
                values.put("cityCode",cityCode);
                values.put("adCode",adCode);
                values.put("aoiName",aoiName);
                values.put("buildingId",buildingId);
                values.put("floor",floor);
                values.put("accuracyStatus",accuracyStatus);
                values.put("dtime",dtime);
                MyDB myDB=new MyDB();
                SQLiteDatabase db =myDB.getWritableDatabase();
                db.beginTransaction();
                try {
                    // 执行多个数据库操作
                    db.insert("gps",null,values);
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    // 发生异常，自动回滚
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                    db.close();
                }
                if (msg!=null){
                    pushmqttgps(msg);

                }
            }
        }
    }

    private void pushmqttgps(MyMqttClient.Msg msg){
        MyDB myDB=new MyDB();
        String s=myDB.getgps(msg.data);
        msg.code="gps";
        msg.data=s;
        Gson gson = new Gson();
        String json2=gson.toJson(msg);
        MyService.myMqttClient.publish("/duma/"+ msg.dumaId,json2);


    }

    public void setfence(MyMqttClient.Msg msg){
        String label="未知";
        int radius=20;
        double lng=0;
        double lat=0;
        try {
            JSONObject jsonObject = new JSONObject(msg.data);
            radius = jsonObject.getInt("radius");
            lng = jsonObject.getDouble("lng");
            lat=jsonObject.getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyDB myDB=new MyDB();
        String sql = String.format(
                "INSERT INTO fence (label, dumaId, radius, lng, lat, dtime) VALUES ('%s', '%s', %d, %.6f, %.6f, %d)",
                label, msg.dumaId, radius, lng, lat, System.currentTimeMillis());
        if (!myDB.execSQL(sql)){
            echo.print("存储失败");
            return;
        }
        echo.print("围栏创建成功");
        addFence(lat,lng,radius);





    }


    //以下为地理围栏
    GeoFenceClient mGeoFenceClient=null;
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";

    //创建回调监听
    GeoFenceListener fenceListenter =new GeoFenceListener() {
        @Override
        public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
            if(i == GeoFence.ADDGEOFENCE_SUCCESS){//判断围栏是否创建成功
//                tvReult.setText("添加围栏成功!!");
                //geoFenceList是已经添加的围栏列表，可据此查看创建的围栏
                echo.print("添加围栏成功");
            } else {
//                tvReult.setText("添加围栏失败!!");
                echo.print("添加围栏失败");

            }

        }
    };

    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                //解析广播内容
                //获取Bundle
                Bundle bundle = intent.getExtras();
                //获取围栏行为：
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                switch (status){
                    case GEOFENCE_IN:
                        echo.print("围栏内");
                        break;
                    case GEOFENCE_OUT:
                        echo.print("围栏外");
                        echo.toast("围栏外");
                        break;
                    case GEOFENCE_STAYED:
                        echo.print("在围栏内10分肿");
                        break;
                    default:break;
                }
                //获取自定义的围栏标识：
                String customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                //获取围栏ID:
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                //获取当前有触发的围栏对象：
                GeoFence fence = bundle.getParcelable(GeoFence.BUNDLE_KEY_FENCE);

            }
        }
    };

    private void createFence(){

        mGeoFenceClient = new GeoFenceClient(context);
        //设置希望侦测的围栏触发行为，默认只侦测用户进入围栏的行为
        //public static final int GEOFENCE_IN 进入地理围栏
        //public static final int GEOFENCE_OUT 退出地理围栏
        //public static final int GEOFENCE_STAYED 停留在地理围栏内10分钟

        mGeoFenceClient.setActivateAction(GEOFENCE_IN|GEOFENCE_OUT|GEOFENCE_STAYED);
        mGeoFenceClient.setGeoFenceListener(fenceListenter);//设置回调监听
        //创建并设置PendingIntent
        mGeoFenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        registBoast();
    }
    public void addFence(double lat,double lon,float radius){
        if (mGeoFenceClient==null){
            createFence();
        }
        //创建一个中心点坐标
        DPoint centerPoint = new DPoint();
        //设置中心点纬度
        centerPoint.setLatitude(lat);
        //设置中心点经度
        centerPoint.setLongitude(lon);
        mGeoFenceClient.addGeoFence (centerPoint,radius,"999");

    }
    private void clearFence(){
        //会清除所有围栏
        mGeoFenceClient.removeGeoFence();
    }

    private void registBoast(){
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(GEOFENCE_BROADCAST_ACTION);
        context.registerReceiver(mGeoFenceReceiver, filter);

    }



}
