package com.easygaadi.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.easygaadi.trucksmobileapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 04-03-2017.
 */
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {

    private JSONArray devicesArray;
    private List<String> selectedDevices;
    private Activity context;
    private LayoutInflater inflater;
    private ClickedPosition clickedPosition;
    public DevicesAdapter(JSONArray devicesArray, Activity context, List<String> selectedDevices,ClickedPosition clickedPosition) {
        this.devicesArray = devicesArray;
        this.selectedDevices = selectedDevices;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.clickedPosition = clickedPosition;
    }

    public interface ClickedPosition{
        void clickedPositon(String deviceID);
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder viewholder, final int position) {
        try {
            final JSONObject deviceObject= devicesArray.getJSONObject(position);
            viewholder.title_tv.setText(deviceObject.getString("deviceID"));
            if(selectedDevices.size()>0 && selectedDevices.contains(deviceObject.getString("deviceID"))){
                viewholder.checkBox.setChecked(true);
            }else{
                viewholder.checkBox.setChecked(false);
            }
            viewholder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickedPosition!=null){
                        try {
                            clickedPosition.clickedPositon(deviceObject.getString("deviceID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return devicesArray.length();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{
        public CheckBox checkBox;
        public TextView title_tv;
        private View view;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            checkBox    = (CheckBox) itemView.findViewById(R.id.checkBox);
            title_tv        = (TextView)itemView.findViewById(R.id.title_tv);
            view = itemView;
        }
    }

    public void swap(JSONArray devicesArray,List<String> selectedDevices){
        this.devicesArray = devicesArray;
        this.selectedDevices = selectedDevices;
        notifyDataSetChanged();
    }
}
