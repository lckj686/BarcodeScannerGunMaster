package com.example.leon.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

public class BaseActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();

    ScannerGunManager scannerGunManager;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("scan", "event= " + event);

        if (scannerGunManager.dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerGunManager = new ScannerGunManager(new ScannerGunManager.OnScanListener() {
            @Override
            public void onResult(String code) {
                Log.d(TAG, "code= " + code);
                onScanResult(code);
            }
        });
    }

    public void onScanResult(String code) {

    }

    public ScannerGunManager getScannerGunManager() {
        return scannerGunManager;
    }
}
