package com.favesolution.kemnaker.network;

import android.content.Context;

import com.favesolution.kemnaker.managers.SharedPreference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 12/7/2015 for Kemnaker project.
 */
public class NetworkUtils {
    public static final int TIMEOUT_REQUEST = 5000;
    public static final int MAX_RETRIES_REQUEST = 1;
    public static final float BACKOFF_MULT_REQUEST = 2f;
    public static Map<String,String> getHeaderToken(Context context) {
        String token = SharedPreference.getUserToken(context);
        Map<String,String> data = new HashMap<>();
        data.put("Token",token);
        return data;
    }
}
