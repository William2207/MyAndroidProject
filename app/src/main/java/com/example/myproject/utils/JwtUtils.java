package com.example.myproject.utils;

import android.util.Base64;
import org.json.JSONObject;
public class JwtUtils {
    public static String getUsernameFromToken(String token){
        try{
            //tach payload
            String[] parts = token.split("\\.");
            if(parts.length<2){
                return null;
            }
            String payload = parts[1];
            //decode base 64
            byte[] decodedBytes = Base64.decode(payload,Base64.URL_SAFE);
            String payloadJson = new String(decodedBytes);
            // Parse JSON
            JSONObject jsonObject = new JSONObject(payloadJson);
            return jsonObject.getString("sub");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Lấy thời gian hết hạn từ token
    public static long getExpirationFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return 0;
            }
            String payload = parts[1];

            // Giải mã Base64
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String payloadJson = new String(decodedBytes);

            // Parse JSON
            JSONObject jsonObject = new JSONObject(payloadJson);
            return jsonObject.getLong("exp"); // "exp" là claim cho thời gian hết hạn
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isTokenExpired(String token) {
        long exp = getExpirationFromToken(token);
        long now = System.currentTimeMillis() / 1000;
        return exp < now;
    }
}
