package com.favesolution.kemnaker.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.network.UrlEndpoint;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Daniel on 11/5/2015 for JktOtw project.
 */
public class LampiranAdapter extends RecyclerView.Adapter<LampiranAdapter.LampiranHolder> {
    private List<String> mPhotos;

    public LampiranAdapter(List<String> attributedPhotos) {
        mPhotos = attributedPhotos;
    }

    @Override
    public LampiranHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_lampiran,parent,false);
        return new LampiranHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(LampiranHolder holder, int position) {
        String photo = mPhotos.get(position);
        holder.bindPhotoItems(photo);
    }
    @Override
    public int getItemCount() {
        if (mPhotos.size() > 4) {
            return 4;
        } else {
            return mPhotos.size();
        }
    }

    class LampiranHolder extends RecyclerView.ViewHolder {
        private String mPhoto;
        private Context mContext;
        @Bind(R.id.image_lampiran) ImageView mLampiranImage;
        public LampiranHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mContext = context;
        }
        public void bindPhotoItems(String photo) {
            mPhoto = UrlEndpoint.UPLOAD_BASEURL+photo;
            Glide.with(mContext)
                    .load(mPhoto)
                    .placeholder(R.drawable.bitmap_plain_grey_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mLampiranImage);
        }

    }
}
