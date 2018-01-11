package com.yinhaoxiao.smarthomeprotector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;

public class MonitorService extends Service {
//    private int mDelayTime = 3000;
    public static HttpHandler HttpNotifiRunner;
    private static int RecvPort = 8888;
    private int mSideChannelSize = 50;

    public MonitorService() {

        if (HttpNotifiRunner == null) {
            HttpNotifiRunner = new HttpHandler(RecvPort, this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Background Monitor Service has been started
        Toast.makeText(this, "Monitor service has been activated!", Toast.LENGTH_SHORT).show();

        try {
            HttpNotifiRunner.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * This function constantly retrieves target app's states:
     * VSS
     * TCP_SND
     */
    private void retrieveTargetAppStates() {

    }
}
