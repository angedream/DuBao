package com.zilong.dubao;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showqrQRCode();

    }
    private void showqrQRCode(){
        ImageView qrCodeImageView = findViewById(R.id.qrCode);
        uuid u=new uuid();
        String textToEncode =u.getuuid(this);
        try {
            // 使用 BarcodeEncoder 生成二维码 Bitmap
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(textToEncode, BarcodeFormat.QR_CODE, 400, 400);
            // 显示二维码
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            // 生成失败时的处理
        }

    }

}