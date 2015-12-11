package com.favesolution.kemnaker.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JsonObjectRequest extends Request<JSONObject> {
    private Listener<JSONObject> listener;
    private Map<String, String> params;
    private Priority mPriority;
    private Map<String, String> mHeaders;
    public JsonObjectRequest(String url, Map<String, String> params,
                             Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        setRetryPolicy(new DefaultRetryPolicy(NetworkUtils.TIMEOUT_REQUEST,
                NetworkUtils.MAX_RETRIES_REQUEST,
                NetworkUtils.BACKOFF_MULT_REQUEST));
    }

    public JsonObjectRequest(int method, String url, Map<String, String> params,
                             Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        setRetryPolicy(new DefaultRetryPolicy(NetworkUtils.TIMEOUT_REQUEST,
                NetworkUtils.MAX_RETRIES_REQUEST,
                NetworkUtils.BACKOFF_MULT_REQUEST));
    }
    protected Map<String, String> getParams()
            throws AuthFailureError {
        return params;
    };

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }


    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    public Priority getPriority() {
        return mPriority == null ? Priority.NORMAL : mPriority;
    }

    public void setHeaders(Map<String,String> headers) {
        this.mHeaders = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }
}