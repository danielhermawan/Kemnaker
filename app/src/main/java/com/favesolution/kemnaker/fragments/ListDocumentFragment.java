package com.favesolution.kemnaker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.activities.DetailDocumentActivity;
import com.favesolution.kemnaker.activities.SingleFragmentActivity;
import com.favesolution.kemnaker.managers.DocumentEvent;
import com.favesolution.kemnaker.models.Dokumen;
import com.favesolution.kemnaker.models.User;
import com.favesolution.kemnaker.network.JsonObjectRequest;
import com.favesolution.kemnaker.network.RequestQueueSingleton;
import com.favesolution.kemnaker.network.requests.DokumenRequest;
import com.favesolution.kemnaker.utils.ListConstant;
import com.favesolution.kemnaker.views.DividerItemDecoration;
import com.favesolution.kemnaker.views.adapters.DokumenAdapter;
import com.favesolution.kemnaker.views.adapters.RecyclerViewLoadingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Daniel on 12/5/2015 for Kemnaker project.
 */
public class ListDocumentFragment extends Fragment {
    private static final String ARGS_USER = "args_user";
    private static final String ARGS_MENU = "args_menu";
    private User mUser;
    private String mMenu;
    private List<Dokumen> mDokumenList = new ArrayList<>();
    private boolean isLoaded;
    private DokumenAdapter mDokumenAdapter;
    private int mNextPage=0;
    @Bind(R.id.spinner_tipe_dokumen) Spinner mTipeSpinner;
    @Bind(R.id.progressbar) ProgressBar mProgressBar;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.text_placeholder) TextView mPlaceholderText;
    public static ListDocumentFragment newInstance(String menu,User user) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_USER, user);
        args.putString(ARGS_MENU,menu);
        ListDocumentFragment fragment = new ListDocumentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mUser = getArguments().getParcelable(ARGS_USER);
        mMenu = getArguments().getString(ARGS_MENU);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_list_document,container,false);
        ButterKnife.bind(this, v);
        showProgress(true);
        if (mMenu.equalsIgnoreCase(ListConstant.MENU_DOKUMEN)) {
            ((SingleFragmentActivity) getActivity()).setTitleToolbar("Dokumen Masuk");
        } else {
            ((SingleFragmentActivity) getActivity()).setTitleToolbar("Arsip");
        }

        final List<String> tipe = Dokumen.getTipeDokumenByUser(mUser,mMenu);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity()
                ,android.R.layout.simple_spinner_item,tipe);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTipeSpinner.setAdapter(spinnerAdapter);
        mTipeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipeSelected = tipe.get(position);
                if (tipeSelected.equalsIgnoreCase("semua")) {
                    mDokumenAdapter.animateTo(mDokumenList);
                } else {
                    List<Dokumen> filteredDokumen = Dokumen.filterDokumenByTipe(mDokumenList,tipeSelected);
                    mDokumenAdapter.animateTo(filteredDokumen);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (mMenu.equalsIgnoreCase(ListConstant.MENU_DOKUMEN)) {
            mPlaceholderText.setText(R.string.list_placeholder_empty_document);
        } else {
            mPlaceholderText.setText(R.string.list_placeholder_empty_arsip);
        }
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mDokumenAdapter = new DokumenAdapter(mRecyclerView, mDokumenList, new RecyclerViewLoadingAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mNextPage != 0) {
                    loadMoreDocument();
                }

            }
        }, new DokumenAdapter.OnDokumenClick() {
            @Override
            public void dokumenClick(Dokumen dokumen) {
                startActivity(DetailDocumentActivity.newIntent(getActivity(),mUser,dokumen.getRemoteId(),mMenu));
            }
        });
        mRecyclerView.setAdapter(mDokumenAdapter);

        refreshDocument();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoaded) {
                    showProgress(true);
                    refreshDocument();
                }
            }
        });
        return v;
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
            /*Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);*/
            for (int i = 0; i < mDokumenList.size(); i++) {
                if (documentEvent.mDokumen.getRemoteId().equals(mDokumenList.get(i).getRemoteId())) {
                    mDokumenList.remove(i);
                    break;
                }
            }
            mDokumenAdapter.resetItems(mDokumenList);
            checkPlaceholder();
        }
    }
    private void refreshDocument() {
        RequestQueueSingleton.getInstance(getActivity())
                .getRequestQueue()
                .cancelAll(this);
        mNextPage=0;
        JsonObjectRequest dokumenRequest = DokumenRequest.getDocuments(getActivity(),
                mMenu,mNextPage, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
                try {
                    JSONArray dokumenArray = response.getJSONArray("dokumen");
                    mDokumenList = Dokumen.fromJson(dokumenArray);
                    Dokumen.saveDeleteListDokumen(mDokumenList);
                    mDokumenAdapter.resetItems(mDokumenList);
                    isLoaded = true;
                    checkPlaceholder();
                    if (response.has("next_offset")) {
                        mNextPage = response.getInt("next_offset");
                        Log.d("test",mNextPage+"");
                    } else {
                        mDokumenAdapter.stopLoading();
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(dokumenRequest, this);
    }

    private void loadMoreDocument() {
        RequestQueueSingleton.getInstance(getActivity())
                .getRequestQueue()
                .cancelAll(this);
        JsonObjectRequest dokumenRequest = DokumenRequest.getDocuments(getActivity(),
                mMenu,mNextPage, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dokumenArray = response.getJSONArray("dokumen");
                            List<Dokumen> dokumens = Dokumen.fromJson(dokumenArray);
                            Dokumen.addListDokumen(dokumens);
                            mDokumenList.addAll(dokumens);
                            mDokumenAdapter.addItems(dokumens);
                            if (response.has("next_offset")) {
                                mNextPage = response.getInt("next_offset");
                            } else {
                                mNextPage = 0;
                                mDokumenAdapter.stopLoading();
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(dokumenRequest, this);
    }
    private void checkPlaceholder() {
        if (mDokumenList.size() > 0) {
            mPlaceholderText.setVisibility(View.GONE);
        } else {
            mPlaceholderText.setVisibility(View.VISIBLE);
        }
    }
    private void showProgress(boolean isShow) {
        if (isShow) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
