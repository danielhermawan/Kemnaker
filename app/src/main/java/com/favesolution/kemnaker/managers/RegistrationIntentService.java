package com.favesolution.kemnaker.managers;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.network.JsonObjectRequest;
import com.favesolution.kemnaker.network.NetworkUtils;
import com.favesolution.kemnaker.network.RequestQueueSingleton;
import com.favesolution.kemnaker.network.requests.UserRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Daniel on 12/6/2015 for Kemnaker project.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    public static final String REGISTRATION_GCM_COMPLETE = "registration_gcm_complete";
    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            String oldGcm = SharedPreference.getGcmToken(this);
            if (!oldGcm.equalsIgnoreCase(token)) {
                sendRegistrationToServer(token);
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            SharedPreference.setIsGcmTokenSaved(this, false);
        }
    }
    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token){
        JsonObjectRequest gcmRequest = UserRequest.registerGcm(this, token, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("result") == 200) {
                        SharedPreference.setGcmToken(RegistrationIntentService.this, token);
                        SharedPreference.setIsGcmTokenSaved(RegistrationIntentService.this, true);
                    }
                    sendFinishIntent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Failed to complete token refresh");
                SharedPreference.setIsGcmTokenSaved(RegistrationIntentService.this, false);
                sendFinishIntent();
            }
        });
        gcmRequest.setPriority(Request.Priority.HIGH);
        gcmRequest.setRetryPolicy(new DefaultRetryPolicy
                (NetworkUtils.TIMEOUT_REQUEST,3,NetworkUtils.BACKOFF_MULT_REQUEST));
        RequestQueueSingleton.getInstance(this).addToRequestQueue(gcmRequest);
    }
    private void sendFinishIntent() {
        Intent finishIntent = new Intent(REGISTRATION_GCM_COMPLETE);
        LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(finishIntent);
    }
}
