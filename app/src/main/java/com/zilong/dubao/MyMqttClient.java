package com.zilong.dubao;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

public class MyMqttClient {
    MyWebRtc myWebRtc;

    private Handler mWorkHandler;
    private MqttClient client;
    private MqttConnectOptions connOpts;
    private String uuid;
    static public class Msg{
        Msg(String code,String dumaName,String dumaId,String dubaoId){
            this.code=code;
            this.dumaName=dumaName;
            this.dumaId=dumaId;
            this.dubaoId=dubaoId;
        }
        Msg(){}
        String code="";
        String dumaName="";
        String dumaId="";
        String dubaoId="";
        String data="";
    }
    private void print(String s){
        Log.d("mqtt",s);
    }

    private void HandleMsg(String json){
//        String json="{\"code\":\"bind\",\"dumaName\":\"嘟妈\",\"dumaId\":\"f1122aeb-f2b0-400d-9919-eddd2eaebaa2\",\"dubaoId\":\"cfb20ccc-8c53-4434-85bb-a171c3ca7c0c\"}";

        try {
            print(json);
            Gson gson = new Gson();
            Msg msg = gson.fromJson(json, Msg.class);
            if (msg.dubaoId.equals(uuid)&&msg.code.equals("bind")){
                Intent intent = new Intent(app.getContext(), MessageActivity.class);
                intent.putExtra("dumaName",msg.dumaName);
                intent.putExtra("dumaId",msg.dumaId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NotificationMsg notificationMsg=new NotificationMsg();
                notificationMsg.sendNotification(msg.dumaName+"请求绑定",msg.dumaId,intent);
                return;

            }
            switch (msg.code){
            case "offer":
                myWebRtc.createPeerConnection(msg.data);
                break;
                case "ice":
                    myWebRtc.onRemoteIceCandidateReceived(msg.data);
                    break;
                case "changecam":
                    myWebRtc.changeCam();
                    break;
                case "changescreen":
                    myWebRtc.getScreenVideo();
                    break;
            default:break;
            }

        }catch (JsonSyntaxException e){
            e.printStackTrace();
        }



    }



    MyMqttClient(String uuid){
        HandlerThread handlerThread = new HandlerThread("worker");
        handlerThread.start();
        mWorkHandler = new Handler(handlerThread.getLooper());
        this.uuid=uuid;
        Log.d("mqtt",uuid);
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
                client.subscribe("/dubao/"+uuid);
                myWebRtc=new MyWebRtc();

            } catch (MqttException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void connectionLost(Throwable cause) {
            cause.printStackTrace();
            Log.d("mqtt","connectionLost");

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String s=new String(message.getPayload());
            HandleMsg(s);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    protected void _connect(){
        try {
            String url= "tcp://"+MyConfig.mqttip+":"+MyConfig.mqttport;
            String dubaoID=uuid;
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
