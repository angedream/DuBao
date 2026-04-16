package com.zilong.dubao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


class Fish{
    public String name;
    protected int price;
    private float weight;
    public interface CB {
            void onSwim();
    }
    Fish(){
        name="未知鱼";
        price=0;
        weight=0;
    }
    public void swim(CB cb){
        addPrice();
        addPrice(10);
        Log.d("test0",name+"会游泳"+"价格"+price+"体重"+weight);
        cb.onSwim();
    }
    private void addPrice(){
        price++;
        weight++;
    }
    private void addPrice(int num){
        price+=num;
        weight+=num;
    }

}
public class HelloWorldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        Fish fish=new Fish();
        fish.name="鲫鱼";
        fish.swim(new Fish.CB() {
            @Override
            public void onSwim() {
                Log.d("test0","Swim函数已经执行完成");

            }
        });
    }
}