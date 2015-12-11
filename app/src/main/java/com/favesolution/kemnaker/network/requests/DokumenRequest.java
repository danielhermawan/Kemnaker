package com.favesolution.kemnaker.network.requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.favesolution.kemnaker.network.JsonArrayRequest;
import com.favesolution.kemnaker.network.JsonObjectRequest;
import com.favesolution.kemnaker.network.NetworkUtils;
import com.favesolution.kemnaker.network.UrlEndpoint;
import com.favesolution.kemnaker.utils.ListConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 12/8/2015 for Kemnaker project.
 */
public class DokumenRequest {
    public static JsonObjectRequest getDocuments
            (Context context,String tipe,int offset,Response.Listener<JSONObject> listener,Response.ErrorListener errorListener) {
        String url;
        if (tipe.equalsIgnoreCase(ListConstant.MENU_DOKUMEN)) {
            url = getDokumenUrl("getDokumen");
        } else {
            url = getDokumenUrl("getArsip");
        }
        url+="/"+offset;
        JsonObjectRequest request = new JsonObjectRequest(url, null,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
    public static JsonArrayRequest getLampiran
            (Context context,String id,Response.Listener<JSONArray> listener,Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(getDokumenUrl("getLampiran")+"/"+id,null,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
    public static JsonObjectRequest getPenerusan
            (Context context,Response.Listener<JSONObject> listener,Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(getDokumenUrl("getPenerusan"),null,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
    public static JsonObjectRequest teruskanDokumen
            (Context context,String documentId,String target,String catatan,String disposisiId,
             Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        Map<String,String> data = new HashMap<>();
        data.put("dokumen_id",documentId);
        data.put("peran_id",target);
        data.put("catatan",catatan);
        data.put("disposisi_id",disposisiId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,getDokumenUrl("teruskanDokumen"),data,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
    private static String getDokumenUrl(String endpoint) {
        return UrlEndpoint.BASEURL+"dokumenapi/"+endpoint+"/"+UrlEndpoint.APIKEY;
    }

    public static JsonObjectRequest teruskanDokumenKasubdit
            (Context context,String documentId,String target,String catatan,
             Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        Map<String,String> data = new HashMap<>();
        data.put("dokumen_id",documentId);
        data.put("peran_id",target);
        data.put("catatan",catatan);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,getDokumenUrl("teruskanDokumenKasubdit"),data,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
    public static JsonObjectRequest arsipokumen
            (Context context,String documentId, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        Map<String,String> data = new HashMap<>();
        data.put("dokumen_id",documentId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,getDokumenUrl("arsipDokumen"),data,listener,errorListener);
        request.setHeaders(NetworkUtils.getHeaderToken(context));
        return request;
    }
}
