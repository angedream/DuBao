package com.zilong.dubao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MyMqttClient myMqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myMqttClient=new MyMqttClient();
        myMqttClient.connect();
        Button startbtn=findViewById(R.id.sendbtn);
        startbtn.setOnClickListener(v->{
            String topic=((EditText)findViewById(R.id.topic)).getText().toString();
            String content=((EditText)findViewById(R.id.content)).getText().toString();
            myMqttClient.publish(topic,content);
        });

    }

}