package com.example.teayudamos.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushNotification {
    SharePref sharePref;

    PushNotification(Context context) {
        RequestQueue mRequestQue = Volley.newRequestQueue(context);
        sharePref = new SharePref(context);

        JSONObject json = new JSONObject();

        try {
            json.put("to", "/topics/" + sharePref.getSharedPrefString(Constants.USER_ID));
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Detectada caida - TEAyudamos");
            notificationObj.put("body", "Posible caida de " + sharePref.getSharedPrefString(Constants.ALUMN));
            //replace notification with data when went send data
            json.put("notification", notificationObj);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response -> Log.d("MUR", "onResponse: "),
                    error -> Log.d("MUR", "onError: " + error.networkResponse)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAGzIFVIs:APA91bFiXXX9zUz1zG85qYy1Ha-0Nh85gB-Flikv7SxswZhPzwxOhnh7eKBs_lM_QUPphpJ1PwDJzAA_an30mopSKpvIVEtGrMDeSbnjFwLWcU0IfkRDhapIB5WVBKwdE6xlsTNVPdX5");
                    return header;
                }
            };


            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
