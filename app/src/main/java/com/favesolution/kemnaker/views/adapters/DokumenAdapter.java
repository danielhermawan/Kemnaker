package com.favesolution.kemnaker.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.models.Dokumen;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 12/8/2015 for Kemnaker project.
 */
public class DokumenAdapter extends RecyclerViewLoadingAdapter<Dokumen> {
    public interface OnDokumenClick{
        void dokumenClick(Dokumen dokumen);
    }
    private OnDokumenClick mOnDokumenClick;
    public DokumenAdapter(RecyclerView recyclerView, List<Dokumen> dataSet, OnLoadMoreListener onLoadMoreListener,OnDokumenClick dokumenClick) {
        super(recyclerView, dataSet, onLoadMoreListener);
        mOnDokumenClick = dokumenClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_document,parent,false);
        return new DokumenHolder(parent.getContext(),v,mOnDokumenClick);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        DokumenHolder holder = (DokumenHolder) genericHolder;
        Dokumen dokumen = getItem(position);
        holder.bindItem(dokumen);
    }

    class DokumenHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        private OnDokumenClick mOnDokumenClick;
        private Dokumen mDokumen;
        private Context mContext;
        @Bind(R.id.text_date) TextView mDateText;
        @Bind(R.id.text_document_name) TextView mDocumenNameText;
        @Bind(R.id.text_document_perihal) TextView mDocumentPerihal;
        @Bind(R.id.view_color_type) View mViewColor;
        public DokumenHolder(Context context,View itemView, OnDokumenClick onDokumenClick) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            mOnDokumenClick = onDokumenClick;
            itemView.setOnClickListener(this);
        }
        public void bindItem(Dokumen dokumen) {
            mDokumen = dokumen;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            mDateText.setText(format.format(dokumen.getTanggalTerima()));
            mDocumenNameText.setText(dokumen.getNoAgenda());
            mDocumentPerihal.setText(dokumen.getPerihal());
            if (dokumen.getNamaTipe().equalsIgnoreCase("rahasia")) {
                mViewColor.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_red));
            }
            else if (dokumen.getNamaTipe().equalsIgnoreCase("biasa")) {
                mViewColor.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_green));
            } else {
                mViewColor.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_orange));
            }
        }
        @Override
        public void onClick(View v) {
            mOnDokumenClick.dokumenClick(mDokumen);
        }
    }
}
