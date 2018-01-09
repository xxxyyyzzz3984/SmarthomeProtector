package com.yinhaoxiao.smarthomeprotector;

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

    public HttpHandler(int port) {
        super(port);
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
            Map<String, String> params = session.getParms();
            session.parseBody(params);
            AlertIP = params.get("alertIP");
            AlertMAC = params.get("alertMAC");
            TargetIP = params.get("targetIP");
            TargetMAC = params.get("targetMAC");
            DgOP = params.get("dgOP");

        } catch (IOException | ResponseException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("");
    }

}
