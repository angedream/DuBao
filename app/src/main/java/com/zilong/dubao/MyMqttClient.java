package com.zilong.dubao;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

public class MyMqttClient {
    private Handler mWorkHandler;
    private MqttClient client;
    private MqttConnectOptions connOpts;
    MyMqttClient(){
        HandlerThread handlerThread = new HandlerThread("worker");
        handlerThread.start();
        mWorkHandler = new Handler(handlerThread.getLooper());
    }

    public void connect() {
        mWorkHandler.post(()->{
            _connect();

        });

    }
    private MqttCallbackExtended callbackExtendedallback =new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.d("mqtt","connectComplete");
            try {
                client.subscribe("/duma/#");
            } catch (MqttException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.d("mqtt","connectionLost");

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String s=new String(message.getPayload());
            Log.d("mqtt","主题是:"+topic);
            Log.d("mqtt","内容是::"+s);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    protected void _connect(){
        try {
            String url= "tcp://"+MyConfig.mqttip+":"+MyConfig.mqttport;
            String dubaoID="0001";
            Log.d("mqtt",url);
            client=new MqttClient(url, "dubao_server"+dubaoID, new MemoryPersistence());
            connOpts=new MqttConnectOptions();
            connOpts.setCleanSession(true);// 重连接是否清理会话
            connOpts.setConnectionTimeout(10);
            connOpts.setKeepAliveInterval(60);//心跳间隔（秒）
            connOpts.setAutomaticReconnect(true);
            String s="嘟宝异常掉线了";
            connOpts.setWill("/dubao/will",s.getBytes(StandardCharsets.UTF_8),0,false);
            client.setCallback(callbackExtendedallback);
            _connectionMQTTServer();

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    protected  void _connectionMQTTServer(){
        while (true){
            try {
                client.connect(connOpts);
                break;
            } catch (MqttException e) {
                Log.d("mqtt","连接失败");
                e.printStackTrace();
            }
            try {
                Thread.sleep(5*1000);
                Log.d("mqtt","准备重新连接");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(String topic,String msg){
        publish(topic,msg,0,false);
    }
    public void publish(String topic,String msg,int qos,boolean retained){
        mWorkHandler.post(()->{
            try {
                MqttMessage message=new MqttMessage(msg.getBytes(StandardCharsets.UTF_8));
                message.setQos(qos);
                message.setRetained(retained);
                client.publish(topic,message);
            } catch (MqttException e) {
                e.printStackTrace();
            }

        });
    }
    public void colse(){
        mWorkHandler.post(()->{
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }

        });

    }

}
