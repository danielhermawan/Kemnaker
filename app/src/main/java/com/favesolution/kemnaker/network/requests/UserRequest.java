package com.favesolution.kemnaker.network.requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.favesolution.kemnaker.managers.SharedPreference;
import com.favesolution.kemnaker.network.JsonObjectRequest;
import com.favesolution.kemnaker.network.NetworkUtils;
import com.favesolution.kemnaker.network.UrlEndpoint;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 12/6/2015 for Kemnaker project.
 */
public class UserRequest {
    public static JsonObjectRequest loginUser(String username,String password
            ,Response.Listener<JSONObject> listener,Response.ErrorListener errorListener) {
        Map<String,String> data = new HashMap<>();
        data.put("username",username);
        data.put("password",password);
        return new JsonObjectRequest(Request.Method.POST, getUserUrl("login"), data, listener,errorListener);
    }
    public static JsonObjectRequest logoutUser
            (Context context, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener) {
        Map<String,String> data = new HashMap<>();
        if (SharedPreference.getIsGcmTokenSaved(context)) {
            data.put("gcm_token", SharedPreference.getGcmToken(context));
        } else {
            data.put("dada","dada");
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,getUserUrl("logout"),data,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        SharedPreference.setUserToken(context, "");
        SharedPreference.setGcmToken(context,"");
        SharedPreference.setIsGcmTokenSaved(context, false);
        return request;
    }
    public static JsonObjectRequest dashboardUser
            (Context context, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(getUserUrl("getDashboard"),null,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
    public static JsonObjectRequest registerGcm
            (Context context,String gcmToken,Response.Listener<JSONObject> listener,Response.ErrorListener errorListener) {
        Map<String,String> data = new HashMap<>();
        data.put("token_gcm", gcmToken);
        String oldGcm = SharedPreference.getGcmToken(context);
        if (!oldGcm.equalsIgnoreCase("") && !oldGcm.equals(gcmToken) ) {
            data.put("old_gcm",oldGcm);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,getUserUrl("registerGcm"),data,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
    private static String getUserUrl(String endpoint) {
        return UrlEndpoint.BASEURL+"userapi/"+endpoint+"/"+UrlEndpoint.APIKEY;
    }
}
