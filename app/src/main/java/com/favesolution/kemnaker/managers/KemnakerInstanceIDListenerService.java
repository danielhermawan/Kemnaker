package com.favesolution.kemnaker.managers;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class KemnakerInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = "MyInstanceIDLS";
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        //// TODO: 12/9/2015 Refresh token delete old token from server
    }
}
