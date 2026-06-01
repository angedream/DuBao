package com.zilong.dubao;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class MyWebRtc {
    private EglBase eglBase;
    private PeerConnectionFactory factory;
    PeerConnection peerConnection;
    Context context;
    DataChannel mdataChannel;
    MyWebRtc(){
        this.context=app.getContext();
        init();

    }
    private void print(String s){
        Log.d("mqtt",s);

    }
    private void init() {
        eglBase = EglBase.create();
        // 初始化 WebRTC
        PeerConnectionFactory.InitializationOptions options =
                PeerConnectionFactory.InitializationOptions
                        .builder(context)
                        .createInitializationOptions();

        PeerConnectionFactory.initialize(options);
        factory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(),true,true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBase.getEglBaseContext()))
                .createPeerConnectionFactory();

    }
    public void createPeerConnection(String remoteSdp) {
        List<PeerConnection.IceServer> iceServers=new ArrayList <>();
        iceServers.add(PeerConnection.IceServer.builder("stun:fly.angerdream21.top:3478").createIceServer());
        PeerConnection.RTCConfiguration config =new PeerConnection.RTCConfiguration(iceServers);
        peerConnection=factory.createPeerConnection(config, new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {

            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                print("onIceConnectionChange"+iceConnectionState.name());
                if(iceConnectionState==PeerConnection.IceConnectionState.FAILED){

                }

            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {

            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                js_IceCandidate js=new js_IceCandidate();
                js.candidate=iceCandidate.sdp;
                js.sdpMid=iceCandidate.sdpMid;
                js.sdpMLineIndex=iceCandidate.sdpMLineIndex;
                Gson gson = new Gson();
                String s = gson.toJson(js);
                pushmsg("ice",s);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

            }

            @Override
            public void onAddStream(MediaStream mediaStream) {

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {

            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                mdataChannel=dataChannel;
                setupDataChannelObserver();
                print("onDataChannel--------------------------");

            }

            @Override
            public void onRenegotiationNeeded() {

            }

            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                print("onAddTrack!!!!!!!!!!!!!!!!!!!!");

            }
        });
        peerConnection.addTrack(getAudioTrack());
        localVideoSender =peerConnection.addTrack(getVideoTrack());
        SessionDescription offer=JSONSessionDescription(remoteSdp);
        peerConnection.setRemoteDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                print("onCreateSuccess");

            }

            @Override
            public void onSetSuccess() {
                print("onSetSuccess");
                createAnswer();
            }

            @Override
            public void onCreateFailure(String s) {
                print(s);

            }

            @Override
            public void onSetFailure(String s) {
                print(s);

            }
        }, offer);
    }

    private AudioTrack getAudioTrack(){
        AudioSource audioSource =
                factory.createAudioSource(
                        new MediaConstraints());

        AudioTrack audioTrack =
                factory.createAudioTrack(
                        "audio_track",
                        audioSource);
        audioTrack.enabled();
        return audioTrack;
    }
    private CameraVideoCapturer mCamCapture;
    private SurfaceTextureHelper surfaceTextureHelper;
    private SurfaceTextureHelper surfaceTextureHelperscreen;

    private RtpSender localVideoSender;
    static Intent i;
    private VideoTrack getVideoTrack(){
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
        VideoCapturer videoCapturer = createCameraCapturer(true);
        VideoSource videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, context.getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(480, 640, 30);
        VideoTrack videoTrack=   factory.createVideoTrack("100", videoSource);
        videoTrack.enabled();
        return videoTrack;
    }


    private void setupDataChannelObserver() {
        if (mdataChannel == null) {

            return;
        }
        mdataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {
                print("webrtc缓冲区变化："+l);

            }

            @Override
            public void onStateChange() {
                DataChannel.State state = mdataChannel.state();
                Log.d("WebRTC", ": " + state);
                print("WebRTC 数据通道状态:"+state);
                if (state == DataChannel.State.OPEN) {
                    Log.d("mqtt", "✅ 数据通道已打开");
                    // 可以发送初始消息
                } else if (state == DataChannel.State.CLOSED) {
                    Log.d("mqtt", "❌ 数据通道已关闭");
                }

            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                if (buffer.binary) {
                    // 二进制数据
                    ByteBuffer byteBuffer = buffer.data;
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
//                    handleBinaryMessage(bytes);
                } else {
                    // 文本消息
                    ByteBuffer byteBuffer = buffer.data;
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String message = new String(bytes);
//                    handleTextMessage(message);
                    print(message);
                    sendMessage("shoudaoshsdasdasd");
                }

            }
        });



    }

    // 发送文本消息
    public void sendMessage(String message) {
        if (mdataChannel != null && mdataChannel.state() == DataChannel.State.OPEN) {
            byte[] bytes = message.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            DataChannel.Buffer dataBuffer = new DataChannel.Buffer(buffer, false);
            boolean success = mdataChannel.send(dataBuffer);
            Log.d("WebRTC", "发送消息: " + message + ", 结果: " + success);
        } else {
            Log.e("WebRTC", "数据通道未打开");
        }
    }


    public void getScreenVideo() {
        surfaceTextureHelperscreen = SurfaceTextureHelper.create("CaptureThread1", eglBase.getEglBaseContext());

        VideoCapturer screenCapturer=new ScreenCapturerAndroid(i, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
            }
        }); // eglBaseContext 是你之前初始化的 EGL 上下文
        VideoSource screenVideoSource = factory.createVideoSource(screenCapturer.isScreencast());
        VideoTrack screenVideoTrack = factory.createVideoTrack("102", screenVideoSource);

        // 2. 初始化并启动 ScreenCapturer
        // 注意：需要提前准备好 SurfaceTextureHelper
        screenCapturer.initialize(surfaceTextureHelperscreen, context.getApplicationContext(), screenVideoSource.getCapturerObserver());
        screenCapturer.startCapture(/* width */ 640, /* height */ 480, /* fps */ 25);

        // 3. 执行替换的关键操作
//        localVideoSender.track().setEnabled(false);
        localVideoSender.setTrack(screenVideoTrack, /* takeOwnership= */ true);
    }

    public void changeCam(){
        if (mCamCapture != null) {
            mCamCapture.switchCamera(null);
        }

    }
    private VideoCapturer createCameraCapturer(boolean isFront) {
        Camera2Enumerator enumerator = new Camera2Enumerator(context.getApplicationContext());
        final String[] deviceNames = enumerator.getDeviceNames();
        for (String deviceName : deviceNames) {
            if (isFront ? enumerator.isFrontFacing(deviceName) : enumerator.isBackFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                mCamCapture = (CameraVideoCapturer) videoCapturer;
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }
    private void createAnswer(){
        peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(this,sessionDescription);
                pushmsg("answer",(SessionDescriptionJSON(sessionDescription)));


            }

            @Override
            public void onSetSuccess() {
                print("answer onSetSuccess");

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, new MediaConstraints());
    }
    public String SessionDescriptionJSON(SessionDescription description){
        try {
            JS_SessionDescription s=new JS_SessionDescription();
            switch (description.type){
                case ANSWER:
                    s.type="answer";
                    break;
                default:break;

            }
            s.sdp=description.description;
            Gson gson = new Gson();
            return  gson.toJson(s);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return "";
        }

    }
    public void onRemoteIceCandidateReceived(String s) {
//        print("发起者收到 onRemoteIceCandidateReceived10"+s);
        peerConnection.addIceCandidate(jsonToIceCandidate(s));
    }
    class JS_SessionDescription{
        String sdp="";
        String type="";
    }
    class js_IceCandidate{
        public  String sdpMid;
        public  int sdpMLineIndex;
        public  String candidate;
        public  String usernameFragment="";
    }
    public IceCandidate jsonToIceCandidate(String s) {
        Gson gson = new Gson();
        js_IceCandidate c = gson.fromJson(s, js_IceCandidate.class);
        return new IceCandidate(c.sdpMid, c.sdpMLineIndex, c.candidate);
    }
    public SessionDescription JSONSessionDescription(String s){
        try {
            Gson gson = new Gson();
            JS_SessionDescription offer = gson.fromJson(s, JS_SessionDescription.class);
            SessionDescription.Type type= SessionDescription.Type.OFFER;
            switch (offer.type) {
                case "offer":
                    type = SessionDescription.Type.OFFER;
                    break;
                case "answer":
                    type = SessionDescription.Type.ANSWER;
                    break;
                case "pranswer":
                    type = SessionDescription.Type.PRANSWER;
                    break;
            }
            return new SessionDescription(type, offer.sdp);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void pushmsg(String code,String data){
        MyMqttClient.Msg msg=new MyMqttClient.Msg();
        msg.dubaoId=MyService.dubaoId;
        msg.dumaId="f1122aeb-f2b0-400d-9919-eddd2eaebaa2";
        msg.dumaName="天使嘟妈";
        msg.code=code;
        msg.data=data;
        Gson gson=new Gson();
        String json=gson.toJson(msg);
        MyService.myMqttClient.publish("/duma/"+ msg.dumaId,json);
    }

}
