package com.favesolution.kemnaker.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.favesolution.kemnaker.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class RecyclerViewLoadingAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private final int VISIBLE_THRESHOLD = 5;
    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    private List<T> dataSet;
    private int firstVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private boolean loading = true;
    private boolean endLoading = false;

    public RecyclerViewLoadingAdapter
            (RecyclerView recyclerView, List<T> dataSet, final OnLoadMoreListener onLoadMoreListener) {
        this.dataSet = dataSet;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    totalItemCount = linearLayoutManager.getItemCount();
                    visibleItemCount = linearLayoutManager.getChildCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading && !endLoading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                        // End has been reached

                        addItem(null);
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }
    public void stopLoading() {
        endLoading = true;
        removeItem(null);
    }

    public void resetItems(@NonNull List<T> newDataSet) {
        loading = true;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;

        dataSet.clear();
        addItems(newDataSet);
    }

    public void addItems(@NonNull List<T> newDataSetItems) {
        dataSet.addAll(newDataSetItems);
        notifyDataSetChanged();
    }
    public void addItem(T item) {
        if (!dataSet.contains(item)) {
            dataSet.add(item);
            notifyItemInserted(dataSet.size() - 1);
        }
    }

    public void removeItem(T item) {
        int indexOfItem = dataSet.indexOf(item);
        if (indexOfItem != -1) {
            this.dataSet.remove(indexOfItem);
            notifyItemRemoved(indexOfItem);
        }
    }

    public T getItem(int index) {
        if (dataSet != null && dataSet.get(index) != null) {
            return dataSet.get(index);
        } else {
            throw new IllegalArgumentException("Item with index " + index + " doesn't exist, dataSet is " + dataSet);
        }
    }
    public void moveItem(int fromPosition, int toPosition) {
        final T model = dataSet.remove(fromPosition);
        dataSet.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    public void animateTo(List<T> newDataset) {
        applyAndAnimateRemovals(newDataset);
        applyAndAnimateAdditions(newDataset);
        applyAndAnimateMovedItems(newDataset);
    }
    private void applyAndAnimateRemovals(List<T> newDataset) {
        for (int i = dataSet.size() - 1; i >= 0; i--) {
            T data = dataSet.get(i);
            if (!newDataset.contains(data)) {
                removeItem(data);
            }
        }
    }
    private void applyAndAnimateAdditions(List<T> newDataset) {
        for (int i = newDataset.size() - 1; i >= 0; i--) {
            T data = newDataset.get(i);
            if (!dataSet.contains(data)) {
                addItem(data);
            }
        }
    }
    private void applyAndAnimateMovedItems(List<T> newDataset) {
        for (int toPosition = newDataset.size() - 1; toPosition >= 0; toPosition--) {
            final T model = newDataset.get(toPosition);
            final int fromPosition = dataSet.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position) != null ? ITEM_VIEW_TYPE_BASIC : ITEM_VIEW_TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_BASIC) {
            return onCreateBasicItemViewHolder(parent, viewType);
        } else if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent, viewType);
        } else {
            throw new IllegalStateException("Invalid type, this type ot items " + viewType + " can't be handled");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        if (getItemViewType(position) == ITEM_VIEW_TYPE_BASIC) {
            onBindBasicItemView(genericHolder, position);
        } else {
            onBindFooterView(genericHolder, position);
        }
    }

    public abstract RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position);

    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_progressbar, parent, false);
        return new ProgressViewHolder(v);
    }

    public void onBindFooterView(RecyclerView.ViewHolder genericHolder, int position) {
        ((ProgressViewHolder) genericHolder).progressBar.setIndeterminate(true);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.progressbar)
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}