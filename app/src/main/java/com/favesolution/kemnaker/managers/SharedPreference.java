package com.favesolution.kemnaker.managers;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPreference {
    private static final String PREF_USER_TOKEN = "user_token";
    private static final String PREF_GCM_TOKEN_SAVE_SERVER ="gcm_token_save_server";
    private static final String PREF_GCM_TOKEN = "gmc_token";
    public static String getUserToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USER_TOKEN,null);
    }
    public static void setUserToken(Context context,String userToken) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_USER_TOKEN,userToken)
                .apply();
    }
    public static boolean getIsGcmTokenSaved(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_GCM_TOKEN_SAVE_SERVER, false);
    }
    public static void setIsGcmTokenSaved(Context context, boolean isSended) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_GCM_TOKEN_SAVE_SERVER, isSended)
                .apply();
    }
    public static String getGcmToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_GCM_TOKEN, "");
    }
    public static void setGcmToken(Context context,String gcmToken) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_GCM_TOKEN, gcmToken)
                .apply();
    }
}
