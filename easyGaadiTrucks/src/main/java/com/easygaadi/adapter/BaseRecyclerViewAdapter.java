package com.easygaadi.adapter;

/**
 * Created by ibraincpu6 on 05-12-2016.
 */

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRecyclerViewAdapter<T> extends Adapter<BaseRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private List<T> mList;

    public abstract void bindItem(T var1, BaseRecyclerViewAdapter.ViewHolder var2, Context var3, int var4);

    @LayoutRes
    public abstract int getResource(int var1);

    public BaseRecyclerViewAdapter(Context context, List<T> mList) {
        this.mContext = context;
        this.mList = mList;
    }

    public BaseRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.getResource(viewType), parent, false);
        return new BaseRecyclerViewAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
        this.bindItem(this.mList.get(position), holder, this.mContext, position);
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public T getItem(int index) {
        return this.mList != null && index < this.mList.size()?this.mList.get(index):null;
    }

    public Context getContext() {
        return this.mContext;
    }

    public void setList(List<T> list) {
        this.mList = list;
    }

    public List<T> getList() {
        return this.mList;
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        private Map<Integer, View> mMapView = new HashMap();

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void initViewById(int id) {
            View view = this.itemView != null?this.itemView.findViewById(id):null;
            if(view != null) {
                this.mMapView.put(Integer.valueOf(id), view);
            }

        }

        public View getView(int id) {
            if(this.mMapView.containsKey(Integer.valueOf(id))) {
                return (View)this.mMapView.get(Integer.valueOf(id));
            } else {
                this.initViewById(id);
                return (View)this.mMapView.get(Integer.valueOf(id));
            }
        }
    }
}
