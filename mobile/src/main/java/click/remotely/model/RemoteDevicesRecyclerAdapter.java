package click.remotely.model;

import android.annotation.TargetApi;
import android.content.pm.ServiceInfo;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
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

    private SortedList<DeviceInfo> mDataSet;


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
    public RemoteDevicesRecyclerAdapter() {

        mDataSet = new SortedList<DeviceInfo>(DeviceInfo.class, sortedListCallback);
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

        DeviceInfo deviceInfo = mDataSet.get(position);
        viewHolder.remoteDeviceTextView.setText(deviceInfo.getDeviceName());

        if(deviceInfo.getType() == DeviceInfo.Type.REMOTELY_FOUND) {
            // remote devices (bonjour detected)
            viewHolder.remoteDeviceImageView.setImageResource(R.drawable.ic_remote_devices_white_24dp);
        } else if(deviceInfo.getType() == DeviceInfo.Type.USER_DEFINED) {
            // user defined devices
            viewHolder.remoteDeviceImageView.setImageResource(R.drawable.ic_user_devices_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // Adapter Helper methods
    public DeviceInfo get(int position) {
        return mDataSet.get(position);
    }

    public int add(DeviceInfo item) {
        return mDataSet.add(item);
    }

    public int indexOF(DeviceInfo item) {
        return mDataSet.indexOf(item);
    }

    public void updateItemAt(int position, DeviceInfo item) {
        mDataSet.updateItemAt(position, item);
    }

    public void addAll(List<DeviceInfo> items) {

        mDataSet.beginBatchedUpdates();
        for(DeviceInfo item : items) {
            mDataSet.add(item);
        }
        mDataSet.endBatchedUpdates();
    }

    public void addAll(DeviceInfo[] items) {
        addAll(Arrays.asList(items));
    }

    public boolean remove(DeviceInfo item) {
        return mDataSet.remove(item);
    }

    public DeviceInfo removeItemAt(int position) {
        return mDataSet.removeItemAt(position);
    }

    public void clear() {
        mDataSet.beginBatchedUpdates();
        // remove items at end, to avoid unnecessary array shifting
        while(mDataSet.size() > 0) {
            mDataSet.removeItemAt(mDataSet.size() -1);
        }
        mDataSet.endBatchedUpdates();
    }

    // Implementation of mDataSet SortedList callback SortedList.Callback<DeviceInfo>
    SortedList.Callback<DeviceInfo> sortedListCallback = new SortedList.Callback<DeviceInfo>() {

        @Override
        public int compare(DeviceInfo o1, DeviceInfo o2) {
            return o1.getDeviceName().compareTo(o2.getDeviceName());
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(DeviceInfo oldItem, DeviceInfo newItem) {
            return oldItem.toString().equals(newItem.toString());
        }

        @Override
        public boolean areItemsTheSame(DeviceInfo item1, DeviceInfo item2) {
            return item1.equals(item2);
        }
    };
}
