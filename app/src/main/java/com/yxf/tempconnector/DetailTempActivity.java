package com.yxf.tempconnector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.proton.temp.connector.TempConnectorManager;
import com.proton.temp.connector.bean.ConnectionType;
import com.proton.temp.connector.bean.DeviceBean;
import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.DataListener;
import com.wms.logger.Logger;

import java.util.List;

public class DetailTempActivity extends AppCompatActivity {
    TextView txtTemp;
    Button btnDisconnect;

    private ConnectStatusListener connectStatusListener = new ConnectStatusListener() {
        @Override
        public void onConnectSuccess() {
            super.onConnectSuccess();
            Toast.makeText(DetailTempActivity.this, "onConnectSuccess", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectFaild() {
            super.onConnectFaild();
            Toast.makeText(DetailTempActivity.this, "onConnectFaild", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect(boolean isManual) {
            super.onDisconnect(isManual);
            Toast.makeText(DetailTempActivity.this, "onDisconnect", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void receiveReconnectTimes(int retryCount, int leftCount, long totalTime) {
            super.receiveReconnectTimes(retryCount, leftCount, totalTime);
            Toast.makeText(DetailTempActivity.this, "receiveReconnectTimes", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void receiveNotSampleDevice(String oldMac, String newMac) {
            super.receiveNotSampleDevice(oldMac, newMac);
            Toast.makeText(DetailTempActivity.this, "receiveNotSampleDevice", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void receiveDockerOffline(boolean isOffline) {
            super.receiveDockerOffline(isOffline);
            Toast.makeText(DetailTempActivity.this, "receiveDockerOffline", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showBeforeMeasureDisconnect() {
            super.showBeforeMeasureDisconnect();
            Toast.makeText(DetailTempActivity.this, "showBeforeMeasureDisconnect", Toast.LENGTH_SHORT).show();
        }
    };

    private DataListener dataLisener = new DataListener() {
        @Override
        public void receiveCurrentTemp(float currentTemp) {
            Logger.w("currentTemp===",currentTemp);
        }

        @Override
        public void receiveCurrentTemp(TempDataBean currentTemp) {
            super.receiveCurrentTemp(currentTemp);
            Logger.w("currentTemp TempDataBean===",currentTemp.toString());
        }

        @Override
        public void receiveCurrentTemp(List<TempDataBean> temps) {
            super.receiveCurrentTemp(temps);
            Logger.w("currentTemp list===",temps);
            float currentTemp = temps.get(0).getTemp();
            float algorithmTemp = temps.get(0).getAlgorithmTemp();
            txtTemp.setText(String.format("currentTemp:%s , algorithmTemp : %s ", currentTemp, algorithmTemp));
        }

        @Override
        public void receiveCacheTemp(List<TempDataBean> cacheTemps) {//仅在蓝牙连接模式下起作用
            super.receiveCacheTemp(cacheTemps);
            for (int i = 0; i < cacheTemps.size(); i++) {
                Log.d("cache temp : ",cacheTemps.get(i).getAlgorithmTemp()+"");
            }
        }
    };
    private DeviceBean device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_temp);
        txtTemp=findViewById(R.id.txt_temp);
        btnDisconnect=findViewById(R.id.btn_disconnect);

        device= (DeviceBean) getIntent().getSerializableExtra("device");
        TempConnectorManager.getInstance(device)
//                .setConnectionType(ConnectionType.BROADCAST)//广播方式连接
                .setConnectionType(ConnectionType.BLUETOOTH)//蓝牙方式连接
                .connect(connectStatusListener, dataLisener, true);
        btnDisconnect.setOnClickListener(v -> disConnect());
    }

    private void disConnect(){
        TempConnectorManager.getInstance(device).disConnect();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TempConnectorManager.getInstance(device).disConnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (TempConnectorManager.getInstance(device).isConnected()) {
            TempConnectorManager.getInstance(device).disConnect();
        }
    }
}
