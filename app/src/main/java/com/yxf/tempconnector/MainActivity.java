package com.yxf.tempconnector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;

import com.proton.temp.connector.TempConnectorManager;
import com.proton.temp.connector.bean.DeviceBean;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bluetooth.BleConnector;
import com.proton.temp.connector.bluetooth.callback.OnScanListener;
import com.proton.temp.connector.utils.NewFirmwareUpdateManager;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ScanAdapter adapter;
    private List<DeviceBean> datum = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView scanRecyclerView = findViewById(R.id.id_recycler);
        scanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScanAdapter(datum, this);
        scanRecyclerView.setAdapter(adapter);
        adapter.setListener(new ScanAdapter.ConnectListener() {
            @Override
            public void connectListener(DeviceBean deviceBean) {
                //连接体温贴
                connect(deviceBean);
            }

            @Override
            public void upgrade(String mac) {
                Logger.w("去升级。。。。");
                goToUpgrade(mac);
            }
        });
        scan();
    }

    private void goToUpgrade(String mac) {
        NewFirmwareUpdateManager firmwareUpdateManager = new NewFirmwareUpdateManager(this, mac, new NewFirmwareUpdateManager.FirmwareAdapter() {
            @Override
            public String getFirmwarePath(DeviceType deviceType) {
                /**
                 * 你保存的本地文件路径 如:storage/emulated/o/testfirm/v1.0.0,确保文件存在
                 */
                String localPath = Environment.getExternalStorageDirectory().getPath() + "/testfirm/v1.0.0";
                return localPath;
            }
        });
        firmwareUpdateManager.setOnFirmwareUpdateListener(new NewFirmwareUpdateManager.OnFirmwareUpdateListener() {
            @Override
            public void onSuccess(DeviceType deviceType, String s) {

            }

            @Override
            public void onFail(String s, NewFirmwareUpdateManager.UpdateFailType updateFailType) {

            }

            @Override
            public void onProgress(float v) {
                Logger.w("v===", String.valueOf(v));
            }
        });
        firmwareUpdateManager.setDeviceType(DeviceType.P13);
        firmwareUpdateManager.update();
    }

    private void scan() {
        Logger.w("start scan...");
        if (!BluetoothUtils.isBluetoothOpened()) {
            BluetoothUtils.openBluetooth();
            return;
        }

        datum.clear();
        adapter.notifyDataSetChanged();

        BleConnector.scanDevice(new OnScanListener() {
            @Override
            public void onDeviceFound(DeviceBean device) {
                //看看当前设备是否已经添加
                if (datum != null && datum.size() > 0) {
                    for (DeviceBean tempDevice : datum) {
                        if (tempDevice.getMacaddress().equalsIgnoreCase(device.getMacaddress())) {
                            return;
                        }
                    }
                }
                datum.add(device);
                adapter.notifyItemInserted(datum.size());
            }
        });
    }

    private void connect(DeviceBean device) {
        //停止搜索
        BleConnector.stopScan();
        Intent intent = new Intent(MainActivity.this, DetailTempActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TempConnectorManager.close();
    }
}
