package com.yxf.tempconnector;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.tbruyelle.rxpermissions2.RxPermissions;

public class SpaceActivity extends AppCompatActivity {
    final RxPermissions rxPermissions = new RxPermissions(this);
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space);
        //android 10.0以上设备必须要申请ACCESS_FINE_LOCATION权限才能使用蓝牙相关api
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    } else {
                        finish();
                    }
                });
    }
}
