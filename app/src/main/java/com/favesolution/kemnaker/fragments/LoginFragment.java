package com.favesolution.kemnaker.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.activities.MainActivity;
import com.favesolution.kemnaker.managers.SharedPreference;
import com.favesolution.kemnaker.network.JsonObjectRequest;
import com.favesolution.kemnaker.network.RequestQueueSingleton;
import com.favesolution.kemnaker.network.requests.UserRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;

/**
 * Created by Daniel on 12/2/2015 for Kemnaker project.
 */
public class LoginFragment extends Fragment {
    @Bind(R.id.edit_username) EditText mUsernameEdit;
    @Bind(R.id.edit_password) EditText mPasswordEdit;
    @Bind(R.id.progressbar) ProgressBar mProgressBar;
    @Bind(R.id.view_login_form) View mFormLogin;
    @Bind(R.id.button_login) Button mButtonLogout;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, v);
        mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.register_ime || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        return v;
    }
    @OnClick(R.id.button_login)
    public void attemptLogin() {

        mUsernameEdit.setError(null);
        mPasswordEdit.setError(null);
        String username = mUsernameEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (password.length()==0) {
            mPasswordEdit.setError(getString(R.string.password_error));
            focusView = mPasswordEdit;
            cancel = true;
        }
        if (username.length()==0) {
            mUsernameEdit.setError(getString(R.string.username_error));
            focusView = mUsernameEdit;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            JsonObjectRequest loginRequest = UserRequest.loginUser(username, password, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("status").equalsIgnoreCase("error")) {
                            mUsernameEdit.setError(response.getString("Message"));
                            showProgress(false);
                        } else {
                            SharedPreference.setUserToken(getActivity(),response.getString("token"));
                            Toast.makeText(getActivity(), R.string.login_Success,Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getActivity(),MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            getActivity().finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
            loginRequest.setPriority(Request.Priority.HIGH);
            RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(loginRequest,this);
        }
    }
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mFormLogin.animate().setDuration(shortAnimTime).alpha(show?0:1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFormLogin.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
        mProgressBar.animate().setDuration(shortAnimTime).alpha(show?1:0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }
}
