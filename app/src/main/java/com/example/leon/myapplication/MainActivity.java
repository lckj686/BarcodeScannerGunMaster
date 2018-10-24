package com.example.leon.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private EditText tvDemo;
    private Button btnDemo;

    String Barcode;

    ScannerGunManager scannerGunManager;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "event= " + event);

        if (scannerGunManager.dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDemo = findViewById(R.id.btn_demo);
        btnDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击事件", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, TestBaseActivity.class));
            }
        });

        scannerGunManager = new ScannerGunManager(new ScannerGunManager.OnScanListener() {
            @Override
            public void onResult(String code) {
                Log.d(TAG, "code= " + code);
            }
        });


    }


}
