package com.yxf.tempconnector;

import android.app.Application;

import com.proton.temp.connector.TempConnectorManager;
import com.wms.logger.Logger;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.newBuilder()
                .tag("temp_connector")
                .showThreadInfo(false)
                .methodCount(1)
                .saveLogCount(7)
                .context(this)
                .deleteOnLaunch(false)
                .saveFile(BuildConfig.DEBUG)
                .isDebug(BuildConfig.DEBUG)
                .build();
        TempConnectorManager.init(this);
    }
}
