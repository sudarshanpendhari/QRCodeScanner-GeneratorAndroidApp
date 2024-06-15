package com.codedev.qrcodescanergenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button GenQR,ScanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GenQR=findViewById(R.id.BtnGenerator);
        ScanQR=findViewById(R.id.BtnScanner);
        GenQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,QRGeneratorActivity.class);
                startActivity(intent);
            }
        });

        ScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,QRScannerActivity.class);
                startActivity(intent);
            }
        });
    }
}