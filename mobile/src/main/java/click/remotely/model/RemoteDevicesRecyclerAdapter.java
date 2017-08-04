package click.remotely.model;

import android.annotation.TargetApi;
import android.content.pm.ServiceInfo;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import click.remotely.android.R;

/**
 * Created by michzio on 01/08/2017.
 */

public class RemoteDevicesRecyclerAdapter extends RecyclerView.Adapter<RemoteDevicesRecyclerAdapter.ViewHolder> {

    // bonjour (NSD) found devices
    private Map<String, NsdServiceInfo> mRemoteDevicesMap;
    private List<String> mRemoteDeviceNamesList;

    // user created custom devices
    private Map<String, UserDeviceInfo> mUserDevicesMap;
    private List<String> mUserDeviceNamesList;

    // References to the views for each data item
    // Complex data items may need more than one view per item
    // Provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView remoteDeviceTextView;
        public ImageView remoteDeviceImageView;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            remoteDeviceTextView = (TextView) v.findViewById(R.id.remote_device_name_text_view);
            remoteDeviceImageView = (ImageView) v.findViewById(R.id.remote_device_image_view);
        }
    }

    // Provide suitable constructor depending on the kind of data set
    public RemoteDevicesRecyclerAdapter(Map<String, NsdServiceInfo> remoteDevicesMap, Map<String, UserDeviceInfo> userDevicesMap) {

        this.mRemoteDevicesMap = remoteDevicesMap;
        this.mRemoteDeviceNamesList = new ArrayList<>(remoteDevicesMap.keySet());

        this.mUserDevicesMap = userDevicesMap;
        this.mUserDeviceNamesList = new ArrayList<>(userDevicesMap.keySet());
    }

    public synchronized void notifyRemoteDeviceAdded(String deviceName) {

        if(!mRemoteDeviceNamesList.contains(deviceName)) {
            mRemoteDeviceNamesList.add(deviceName);
            int remoteDevicePosition = mRemoteDeviceNamesList.size()-1;
            notifyItemInserted(remoteDevicePosition);
        } else {
            int remoteDevicePosition = mRemoteDeviceNamesList.indexOf(deviceName);
            notifyItemChanged(remoteDevicePosition);
        }
    }

    public synchronized void notifyRemoteDeviceRemoved(String deviceName) {

        int remoteDevicePosition = mRemoteDeviceNamesList.indexOf(deviceName);
        if(remoteDevicePosition >=0) {
            mRemoteDeviceNamesList.remove(remoteDevicePosition);
            notifyItemRemoved(remoteDevicePosition);
        }
    }

    public synchronized void notifyRemoteDevicesCleared() {

        mRemoteDeviceNamesList.clear();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public synchronized void notifyUserDeviceAdded(String deviceName) {

        if(!mUserDeviceNamesList.contains(deviceName)) {
            mUserDeviceNamesList.add(deviceName);
            int devicePosition = mRemoteDeviceNamesList.size() + mUserDeviceNamesList.size() -1;
            notifyItemInserted(devicePosition);
        } else {
            int userDevicePosition = mUserDeviceNamesList.indexOf(deviceName);
            int devicePosition = mRemoteDeviceNamesList.size() + userDevicePosition;
            notifyItemChanged(devicePosition);
        }
    }

    public synchronized void notifyUserDeviceRemoved(String deviceName) {

        int userDevicePosition = mUserDeviceNamesList.indexOf(deviceName);
        if(userDevicePosition >=0) {
            mUserDeviceNamesList.remove(userDevicePosition);
            int devicePosition = mRemoteDeviceNamesList.size() + userDevicePosition;
            notifyItemRemoved(devicePosition);
        }
    }

    public synchronized void notifyUserDevicesCleared() {

        mUserDeviceNamesList.clear();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    // Create new view (view holder) (using layout inflater)
    @Override
    public RemoteDevicesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_item_remote_device, parent, false);
        // set the view's size, margins, paddings and layout params
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // bind data to the view
    @Override
    @TargetApi(16)
    public void onBindViewHolder(RemoteDevicesRecyclerAdapter.ViewHolder viewHolder, int position) {

        if(position < mRemoteDeviceNamesList.size()) {
            // remote devices (bonjour detected)
            final String deviceName = mRemoteDeviceNamesList.get(position);
            final NsdServiceInfo deviceInfo = mRemoteDevicesMap.get(deviceName);

            viewHolder.remoteDeviceTextView.setText(deviceInfo.getServiceName());
            viewHolder.remoteDeviceImageView.setImageResource(R.drawable.ic_remote_devices_white_24dp);
        } else {
            // user defined devices
            position = position - mRemoteDeviceNamesList.size();
            final String deviceName = mUserDeviceNamesList.get(position);
            final UserDeviceInfo deviceInfo = mUserDevicesMap.get(deviceName);

            viewHolder.remoteDeviceTextView.setText(deviceInfo.getDeviceName());
            viewHolder.remoteDeviceImageView.setImageResource(R.drawable.ic_user_devices_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return mRemoteDeviceNamesList.size() + mUserDeviceNamesList.size();
    }

}
