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
    public MonitorService() {
        HttpNotifiRunner = new HttpHandler(RecvPort);
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

        Thread NetworkThread = new Thread() {
            @Override
            public void run() {
                try {
                    HttpNotifiRunner.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        NetworkThread.start();


        return super.onStartCommand(intent, flags, startId);
    }


}
