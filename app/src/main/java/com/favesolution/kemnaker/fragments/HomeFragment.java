package com.favesolution.kemnaker.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.activities.ListDocumentActivity;
import com.favesolution.kemnaker.managers.DocumentEvent;
import com.favesolution.kemnaker.managers.RegistrationIntentService;
import com.favesolution.kemnaker.models.User;
import com.favesolution.kemnaker.network.JsonObjectRequest;
import com.favesolution.kemnaker.network.RequestQueueSingleton;
import com.favesolution.kemnaker.network.requests.UserRequest;
import com.favesolution.kemnaker.utils.ListConstant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Daniel on 12/3/2015 for Kemnaker project.
 */
@SuppressWarnings("FieldCanBeLocal")
public class HomeFragment extends Fragment{
    private final String TAG = "HomeFragment";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private User mUser;
    private int mCountDoc;
    private int mCountArchive;
    @Bind(R.id.text_nama_user) TextView mNamaText;
    @Bind(R.id.text_peran) TextView mPeranText;
    @Bind(R.id.text_number_document) TextView mNumberDocText;
    @Bind(R.id.text_number_archive) TextView mNumberArcText;
    @Bind(R.id.progressbar) ProgressBar mProgressBar;
    @Bind(R.id.view_content) View mContent;
    @Bind(R.id.view_document) View mViewDocument;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_home,container,false);
        ButterKnife.bind(this,v);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        if (checkPlayServices()) {
            Intent intent = new Intent(getActivity(),RegistrationIntentService.class);
            getActivity().startService(intent);
        }
        JsonObjectRequest dashboardRequest = UserRequest.dashboardUser(getActivity(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
                try {
                    mUser = User.fromJson(response.getJSONObject("user"));
                    mCountDoc = response.getInt("jumlahDokumen");
                    mCountArchive = response.getInt("jumlahArsip");
                    updateUi();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                //Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(dashboardRequest,this);
        showProgress(true);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(RegistrationIntentService.REGISTRATION_GCM_COMPLETE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        RequestQueueSingleton.getInstance(getActivity()).getRequestQueue().cancelAll(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    public void onEvent(DocumentEvent documentEvent) {
        if (documentEvent.mStatus.equals(ListConstant.STATUS_DELETED)) {
            JsonObjectRequest dashboardRequest = UserRequest.dashboardUser(getActivity(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    showProgress(false);
                    try {
                        mUser = User.fromJson(response.getJSONObject("user"));
                        mCountDoc = response.getInt("jumlahDokumen");
                        mCountArchive = response.getInt("jumlahArsip");
                        updateUi();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    //Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(dashboardRequest,this);
            showProgress(true);
        }
    }
    @OnClick(R.id.view_document)
    public void documentClick() {
        startActivity(ListDocumentActivity.newIntent(getActivity(), mUser, ListConstant.MENU_DOKUMEN));
    }

    @OnClick(R.id.view_arhive)
    public void arsipClick() {
        startActivity(ListDocumentActivity.newIntent(getActivity(), mUser, ListConstant.MENU_ARSIP));
    }
    @SuppressLint("SetTextI18n")
    private void updateUi() {
        mNumberDocText.setText(mCountDoc+"");
        mNumberArcText.setText(mCountArchive+"");
        mNamaText.setText(getString(R.string.home_name_user,mUser.getNamaPanggilan()));
        if(mUser.getPeran().toLowerCase().startsWith("kasie")||mUser.getPeran().toLowerCase().startsWith("kasubdit"))
            mPeranText.setText(mUser.getPeran());
        else
            mPeranText.setText(getString(R.string.home_peran,mUser.getPeran()));
        if (mUser.getPeran().equalsIgnoreCase("staff")) {
            mViewDocument.setVisibility(View.GONE);
        }
    }
    private void showProgress(boolean isShow) {
        if (isShow) {
            mProgressBar.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mContent.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode,0)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }
}
