package com.zilong.dubao;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.apis.utils.core.api.AMapUtilCoreApi;

public class GpsGaode {
    private Handler mWorkHandler;

    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            updateShow(amapLocation);
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
                option.setOnceLocation(true);


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


    private void updateShow(AMapLocation amapLocation) {
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
            }
        }
    }


}
