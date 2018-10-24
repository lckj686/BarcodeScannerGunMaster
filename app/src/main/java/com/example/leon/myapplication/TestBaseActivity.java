package com.example.leon.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestBaseActivity extends BaseActivity {


    private TextView tvTestContent;
    private Button btnTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_base);
        tvTestContent = (TextView) findViewById(R.id.tv_test_content);
        btnTest = (Button) findViewById(R.id.btn_test);


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestBaseActivity.this, "点击事件", Toast.LENGTH_SHORT).show();


                getScannerGunManager().setInterrupt(!getScannerGunManager().isInterrupt);
            }
        });
    }


    @Override
    public void onScanResult(String code) {
        super.onScanResult(code);

        tvTestContent.setText(code);
    }
}
