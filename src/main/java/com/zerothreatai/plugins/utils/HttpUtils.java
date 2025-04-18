package com.zerothreatai.plugins.utils;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    private static final String SCAN_API_URL = "https://api.zerothreat.ai/api/scan/devops";

    public static ScanResponse initiateScan(String token, BuildLogger logger) {
        try {
            String requestBody = "{\"token\":\"" + token + "\"}";
            HttpURLConnection conn = (HttpURLConnection) new URL(SCAN_API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.getOutputStream().write(requestBody.getBytes());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();

            // Use the older Gson API
            JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();
            ScanResponse scanResponse = new ScanResponse();
            scanResponse.Status = jsonResponse.get("status").getAsInt();
            scanResponse.Message = jsonResponse.get("message").getAsString();
            scanResponse.Code = jsonResponse.get("code").getAsString();
            scanResponse.ScanStatus = jsonResponse.get("scanStatus").getAsInt();
            scanResponse.Url = jsonResponse.get("url").getAsString();
            return scanResponse;
        } catch (Exception e) {
            logger.addErrorLogEntry("Error initiating scan: " + e.getMessage());
            return new ScanResponse(e.getMessage());
        }
    }

    public static boolean pollScanStatus(String scanId, BuildLogger logger) {
        int status = 1;
        while (status < 4) {
            try {
                TimeUnit.SECONDS.sleep(300);
                HttpURLConnection conn = (HttpURLConnection) new URL(SCAN_API_URL+"/" + scanId).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();

                // Use the older Gson API
                JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();
                status = jsonResponse.get("scanStatus").getAsInt();

                logger.addBuildLogEntry("Scan is inprogress");

            } catch (Exception e) {
                logger.addErrorLogEntry("Error polling scan status: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
