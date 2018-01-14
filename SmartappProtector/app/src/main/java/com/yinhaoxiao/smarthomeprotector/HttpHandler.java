package com.yinhaoxiao.smarthomeprotector;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * This class is intended to receive
 * notification from router in forms of HTTP
 */

public class HttpHandler extends NanoHTTPD {

    public static String AlertIP;
    public static String AlertMAC;
    public static String TargetIP;
    public static String TargetMAC;
    public static String DgOP;
    private Context mMonitorServiceInst;
    private boolean mUserDecision;
    private int mCheckGapTime; // gap time to check user's decision, in ms

    public HttpHandler(int port, Context MonitorServiceInst) {
        super(port);
        System.out.println("Here http init");
        if (mMonitorServiceInst == null) {
            mMonitorServiceInst = MonitorServiceInst;
        }
        this.mCheckGapTime = 100;
    }

    /**
     * The main handle function for retrieving alert notification
     * Parse Rules:
     * alertIP : IP address that sends the dangerous operation
     * alertMAC : MAC address that sends the dangerous operation
     * targetIP : Target IP address of the IoT device
     * targetMAC : Target MAC address of the IoT device
     * dgOP : Description of the dangerous operation
     */
    @Override
    public Response serve(IHTTPSession session) {
        try {
            System.out.println("Here serve0");
            Map<String, String> params = session.getParms();
            session.parseBody(params);
            AlertIP = params.get("alertIP");
            AlertMAC = params.get("alertMAC");
            TargetIP = params.get("targetIP");
            TargetMAC = params.get("targetMAC");
            DgOP = params.get("dgOP");
            System.out.println("Here serve1");

            HandleIP(AlertIP);

            if (mUserDecision) {
                return newFixedLengthResponse("{decision:allow}");
            }
            else {
                return newFixedLengthResponse("{decision:deny}");
            }

        } catch (IOException | ResponseException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("");
    }


    private void HandleIP (String alertIP) {

        // ip address does not match, directly go to AlertActivity
        if (!alertIP.matches(MainActivity.CurrentIP)) {
            Intent alertIntent = new Intent(mMonitorServiceInst, AlertActivity.class);
            alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mMonitorServiceInst.startActivity(alertIntent);
            this.mUserDecision = getUserDecision();
        }

        // ip address matches, need further analysis
        else {
            //First, simply check if the target app is running
            long Min_vss = MonitorService.findMinIndex(MonitorService.mTargetVSSList);
            long Delta_tcpsnd = MonitorService.mTargetTcpSndList.get(MonitorService.mTargetTcpSndList.size()-1)
                    - MonitorService.mTargetTcpSndList.get(0);

            // if the app is not running
            if (Min_vss < 10) {
                Intent alertIntent = new Intent(mMonitorServiceInst, AlertActivity.class);
                alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mMonitorServiceInst.startActivity(alertIntent);
                this.mUserDecision = getUserDecision();
            }

            // if the app did not send a traffic
            else if (Delta_tcpsnd < 10) {
                Intent alertIntent = new Intent(mMonitorServiceInst, AlertActivity.class);
                alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mMonitorServiceInst.startActivity(alertIntent);
                this.mUserDecision = getUserDecision();
            }

            // (TEST)
            else {
                this.mUserDecision = true;
            }
        }
        AlertActivity.UserDecision = -1;
    }

    private boolean getUserDecision() {
        while (AlertActivity.UserDecision < 0) {
            try {
                Thread.sleep(mCheckGapTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (AlertActivity.UserDecision == 1) {
            return true;
        }
        else {
            return false;
        }
    }

}
