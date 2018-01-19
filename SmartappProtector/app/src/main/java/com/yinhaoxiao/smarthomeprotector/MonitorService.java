package com.yinhaoxiao.smarthomeprotector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MonitorService extends Service {
//    private int mDelayTime = 3000;
    public static HttpHandler HttpNotifiRunner;
    private static int RecvPort = 8888;
    private int mSideChannelSize = 30;
    private int mTimeGap4RetrState = 100;
    private ProcessManager.Process mTargetProcess;
    public static List<Long> mTargetVSSList;
    public static List<Long> mTargetTcpSndList;

    public MonitorService() {

        if (HttpNotifiRunner == null) {
            HttpNotifiRunner = new HttpHandler(RecvPort, this);
        }
        mTargetVSSList = new ArrayList<>();
        mTargetTcpSndList = new ArrayList<>();
        mTargetTcpSndList.add(0L);
        mTargetVSSList.add(0L);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("here start service");
                    HttpNotifiRunner.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        // constantly getting target app states constantly
        Thread GetTargetAppStateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(mTimeGap4RetrState);
                        retrieveTargetAppStates();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        GetTargetAppStateThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * This function one time retrieves target app's states:
     * VSS
     * TCP_SND
     */
    private void retrieveTargetAppStates() throws Exception {
        try {
            mTargetProcess = ProcessManager.getParticularProcessInfo(MainActivity.SelectedAppName);
            mTargetVSSList.add(mTargetProcess.vsize);
        }

        catch (IndexOutOfBoundsException e) {
            // cannot get system states of the process, re-initiate and exit

            mTargetTcpSndList.clear();
            mTargetVSSList.clear();
            mTargetTcpSndList.add(0L);
            mTargetVSSList.add(0L);
            return;
        }

        // get tcp snd
        String tcp_snd_str = "";
        Runtime r = Runtime.getRuntime();
        Process process = r.exec("cat /proc/uid_stat/" + Integer.toString(mTargetProcess.uid) + "/tcp_snd");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = in.readLine()) != null) {
            tcp_snd_str += line;
        }

//        System.out.println("tcp data: " + tcp_snd_str);
//        System.out.println("vss data: " + mTargetProcess.vsize);

        try {
            mTargetTcpSndList.add(Long.parseLong(tcp_snd_str));
        }
        catch (NumberFormatException e) {
            mTargetTcpSndList.add(mTargetTcpSndList.get(mTargetTcpSndList.size()-1));
        }

        // if the size greater than preset size
        if (mTargetVSSList.size() > mSideChannelSize) {
            mTargetVSSList.remove(0);
        }
        if (mTargetTcpSndList.size() > mSideChannelSize) {
            mTargetTcpSndList.remove(0);
        }
    }

    public static <T extends Comparable<T>> int findMinIndex(final List<T> xs) {
        int minIndex;
        if (xs.isEmpty()) {
            minIndex = -1;
        } else {
            final ListIterator<T> itr = xs.listIterator();
            T min = itr.next(); // first element as the current minimum
            minIndex = itr.previousIndex();
            while (itr.hasNext()) {
                final T curr = itr.next();
                if (curr.compareTo(min) < 0) {
                    min = curr;
                    minIndex = itr.previousIndex();
                }
            }
        }
        return minIndex;
    }
}
