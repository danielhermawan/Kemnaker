package com.favesolution.kemnaker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.activities.SingleFragmentActivity;
import com.favesolution.kemnaker.managers.DocumentEvent;
import com.favesolution.kemnaker.models.Dokumen;
import com.favesolution.kemnaker.models.User;
import com.favesolution.kemnaker.network.JsonArrayRequest;
import com.favesolution.kemnaker.network.JsonObjectRequest;
import com.favesolution.kemnaker.network.RequestQueueSingleton;
import com.favesolution.kemnaker.network.requests.DokumenRequest;
import com.favesolution.kemnaker.utils.ListConstant;
import com.favesolution.kemnaker.views.adapters.LampiranAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Daniel on 12/6/2015 for Kemnaker project.
 */
public class DetailDocumentFragment extends Fragment{
    private final static String ARGS_USER = "extra_user";
    private final static String ARGS_DOKUMENT_ID = "extra_dokument_id";
    private final static String ARGS_MENU = "extra_menu";
    @Bind(R.id.view_color_type) View mColorView;
    @Bind(R.id.text_document_name) TextView mDocumentText;
    @Bind(R.id.text_agenda) TextView mAgendaText;
    @Bind(R.id.text_tanngal) TextView mTanggalText;
    @Bind(R.id.text_asal) TextView mAsalText;
    @Bind(R.id.text_nomor_tanggal) TextView mNomorTanggalText;
    @Bind(R.id.text_perihal) TextView mPerihalText;
    @Bind(R.id.recyclerview_lampiran) RecyclerView mLampiranRecyclerView;
    @Bind(R.id.spinner_diteruskan) Spinner mTeruskanSpinner;
    @Bind(R.id.spinner_disposisi) Spinner mDisposisiSpinner;
    @Bind(R.id.edit_catatan) EditText mCatatanEdit;
    @Bind(R.id.text_lajur_disposisi) TextView mDisposisiText;
    @Bind(R.id.view_content) View mContent;
    @Bind(R.id.progressbar) ProgressBar mProgressBar;
    @Bind(R.id.button_teruskan) Button mTeruskanButton;
    @Bind(R.id.button_arsip) Button mArsipButton;
    @Bind(R.id.view_penerusan) View mPenerusanView;
    @Bind(R.id.view_disposisi) View mDisposisiView;
    @Bind(R.id.form_disposisi) View mFormDisposisiView;
    private User mUser;
    private String mMenu;
    private Dokumen mDokumen;
    private List<String> mLampiranLink = new ArrayList<>();
    private JSONArray mListDisposisi;
    private JSONArray mListTeruskan;
    public static DetailDocumentFragment newInstance(User user,String id,String menu) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_USER,user);
        args.putString(ARGS_DOKUMENT_ID, id);
        args.putString(ARGS_MENU,menu);
        DetailDocumentFragment fragment = new DetailDocumentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Ada 4 cara : pakai sqllite, startactivityforresult,singleton,event,optional preference
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id= getArguments().getString(ARGS_DOKUMENT_ID);
        mDokumen = Dokumen.getDokumenByRemoteId(id);
        mUser = getArguments().getParcelable(ARGS_USER);
        mMenu = getArguments().getString(ARGS_MENU);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_detail_document, container, false);
        ButterKnife.bind(this, v);
        ((SingleFragmentActivity) getActivity()).setTitleToolbar(mDokumen.getNoAgenda());
        showProgress(false);
        updateUi();

        mLampiranRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), android.support.v7.widget.LinearLayoutManager.HORIZONTAL,false));
        mLampiranRecyclerView.setAdapter(new LampiranAdapter(mLampiranLink));
        JsonArrayRequest lampiranRequest = DokumenRequest.getLampiran(getActivity(), mDokumen.getRemoteId()
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        mLampiranLink.add(response.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mLampiranRecyclerView.setAdapter(new LampiranAdapter(mLampiranLink));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Lampiran tidak bisa diambil", Toast.LENGTH_SHORT).show();
            }
        });
        JsonObjectRequest penerusanRequest = DokumenRequest.getPenerusan(getActivity(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("disposisi")) {
                        mListDisposisi = response.getJSONArray("disposisi");
                        List<String> listDisposisi = new ArrayList<>();
                        for (int i = 0; i < mListDisposisi.length(); i++) {
                            listDisposisi.add(mListDisposisi.getJSONObject(i).getString("lajur_disposisi"));
                        }
                        ArrayAdapter<String> spinnerDisposisiAdapter = new ArrayAdapter<>(getActivity()
                                , android.R.layout.simple_spinner_item, listDisposisi);
                        spinnerDisposisiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mDisposisiSpinner.setAdapter(spinnerDisposisiAdapter);
                    }

                    mListTeruskan = response.getJSONArray("list_diteruskan");
                    List<String> listTeruskan = new ArrayList<>();
                    for (int i = 0; i < mListTeruskan.length(); i++) {
                        listTeruskan.add(mListTeruskan.getJSONObject(i).getString("nama_peran"));
                    }
                    ArrayAdapter<String> spinnerPenerusaniAdapter = new ArrayAdapter<>(getActivity()
                            , android.R.layout.simple_spinner_item, listTeruskan);
                    spinnerPenerusaniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mTeruskanSpinner.setAdapter(spinnerPenerusaniAdapter);
                    mTeruskanButton.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "List penerusan tidak bisa diambil", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(lampiranRequest, this);
        if (!(mUser.getPeran().equalsIgnoreCase("staff") || mMenu.equalsIgnoreCase(ListConstant.MENU_ARSIP)
                || mUser.getPeran().toLowerCase().startsWith("kasie"))) {
            RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(penerusanRequest,this);
        }
        return v;
    }

    @Override
    public void onStop() {
        RequestQueueSingleton.getInstance(getActivity()).getRequestQueue().cancelAll(this);
        super.onStop();
    }

    private void updateUi() {
        if (mDokumen.getNamaTipe().equalsIgnoreCase("rahasia")) {
            mColorView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_red));
        }
        else if (mDokumen.getNamaTipe().equalsIgnoreCase("biasa")) {
            mColorView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_green));
        } else {
            mColorView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_orange));
        }
        if (mUser.getPeran().toLowerCase().startsWith("kasie")||mUser.getPeran().equalsIgnoreCase("staff")
                || mMenu.equalsIgnoreCase(ListConstant.MENU_ARSIP)) {
            mPenerusanView.setVisibility(View.GONE);
        }
        if (mUser.getPeran().toLowerCase().startsWith("kasie")&& !mMenu.equalsIgnoreCase(ListConstant.MENU_ARSIP) ) {
            mTeruskanButton.setVisibility(View.GONE);
            mArsipButton.setVisibility(View.VISIBLE);
        }
        if ((mUser.getPeran().toLowerCase().startsWith("kasubdit")
                ||mUser.getPeran().toLowerCase().startsWith("kasie"))
                && !mMenu.equalsIgnoreCase(ListConstant.MENU_ARSIP)) {
            mFormDisposisiView.setVisibility(View.GONE);
            mDisposisiView.setVisibility(View.VISIBLE);
            mDisposisiText.setText(mDokumen.getLajurDisposisi());
        }
        mDocumentText.setText(mDokumen.getNamaTipe());
        mAgendaText.setText(mDokumen.getNoAgenda());
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        mTanggalText.setText(format.format(mDokumen.getTanggalTerima()));
        mAsalText.setText(mDokumen.getAsalSurat());
        mNomorTanggalText.setText(mDokumen.getNoTanggal());
        mPerihalText.setText(mDokumen.getPerihal());
        mTeruskanButton.setEnabled(false);
    }
    @OnClick(R.id.button_teruskan)
    public void teruskanDokumen() {
        showProgress(true);
        JsonObjectRequest request;
        if (mUser.getPeran().equalsIgnoreCase("direktur")) {
            String idDisposisi = "";
            String idTarget = "";
            try {
                idDisposisi = mListDisposisi.getJSONObject(mDisposisiSpinner.getSelectedItemPosition()).getString("id");
                idTarget = mListTeruskan.getJSONObject(mTeruskanSpinner.getSelectedItemPosition()).getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = DokumenRequest.teruskanDokumen(getActivity(), mDokumen.getRemoteId(),
                    idTarget, mCatatanEdit.getText().toString(), idDisposisi, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            showProgress(false);
                            Toast.makeText(getActivity(), "Dokumen sukses diterukan", Toast.LENGTH_SHORT).show();
                            sendNotifDocument();
                            getActivity().finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            Toast.makeText(getActivity(), "Internet tidak stabil ", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            String idTarget = "";
            try {
                idTarget = mListTeruskan.getJSONObject(mTeruskanSpinner.getSelectedItemPosition()).getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = DokumenRequest.teruskanDokumenKasubdit(getActivity(), mDokumen.getRemoteId(),
                    idTarget, mCatatanEdit.getText().toString(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            showProgress(false);
                            Toast.makeText(getActivity(), "Dokumen sukses diteruskan", Toast.LENGTH_SHORT).show();
                            sendNotifDocument();
                            getActivity().finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            Toast.makeText(getActivity(), "Internet tidak stabil ", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        request.setPriority(Request.Priority.HIGH);
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
    @OnClick(R.id.button_arsip)
    public void arsipDokumen() {
        showProgress(true);
        JsonObjectRequest request = DokumenRequest.arsipokumen(getActivity(), mDokumen.getRemoteId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
                Toast.makeText(getActivity(), "Dokumen sukses diarsipkan", Toast.LENGTH_SHORT).show();
                getActivity().finish();
               sendNotifDocument();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(getActivity(), "Internet tidak stabil ", Toast.LENGTH_SHORT).show();
            }
        });
        request.setPriority(Request.Priority.HIGH);
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
    private void sendNotifDocument() {
        EventBus.getDefault().post(new DocumentEvent(mDokumen, ListConstant.STATUS_DELETED));
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
}
