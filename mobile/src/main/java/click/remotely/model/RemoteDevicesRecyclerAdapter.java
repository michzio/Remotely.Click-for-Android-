package click.remotely.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Button;
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
import click.remotely.android.recycler.SwipeDeleteHelper;
import click.remotely.database.UserDeviceInfoProvider;

/**
 * Created by michzio on 01/08/2017.
 */

public class RemoteDevicesRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SwipeDeleteHelper.AdapterInterface {

    public static interface OnItemClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public static interface OnDeviceConnectionListener {
        public void onDeviceConnection(DeviceInfo deviceInfo);
    }

    private OnDeviceConnectionListener mDeviceConnectionListener;

    public void setOnDeviceConnectionListener(OnDeviceConnectionListener listener) {
        mDeviceConnectionListener = listener;
    }

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_UNDO = 1;

    private Context mContext;
    private SortedList<DeviceInfo> mDataSet;
    private RecyclerView mRecyclerView;
    private int unwindItemPosition = -1;
    private int undoItemsOpen = 0;

    // View Holder for "normal" state item
    // References to the views for each data item
    // Complex data items may need more than one view per item
    // Provide access to all the views for a data item in a view holder
    public class ViewHolderNormal extends RecyclerView.ViewHolder {

        public View layout;
        public TextView remoteDeviceTextView;
        public ImageView remoteDeviceImageView;
        public ViewGroup remoteDeviceDetailsView;
        public TextView remoteDeviceDetailsNameTextView;
        public TextView remoteDeviceDetailsHostTextView;
        public TextView remoteDeviceDetailsPortNumberTextView;
        public Button remoteDeviceDetailsConnectButton;

        public ViewHolderNormal(View v) {
            super(v);
            layout = v;
            remoteDeviceTextView = (TextView) v.findViewById(R.id.remote_device_name_text_view);
            remoteDeviceImageView = (ImageView) v.findViewById(R.id.remote_device_image_view);
            remoteDeviceDetailsView = (ViewGroup) v.findViewById(R.id.remote_device_details_view);
            remoteDeviceDetailsNameTextView = (TextView) remoteDeviceDetailsView.findViewById(R.id.remote_device_details_name_text_view);
            remoteDeviceDetailsHostTextView = (TextView) remoteDeviceDetailsView.findViewById(R.id.remote_device_details_host_text_view);
            remoteDeviceDetailsPortNumberTextView = (TextView) remoteDeviceDetailsView.findViewById(R.id.remote_device_details_port_number_text_view);
            remoteDeviceDetailsConnectButton = (Button) remoteDeviceDetailsView.findViewById(R.id.remote_device_details_connect_btn);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mItemClickListener != null) {
                        mItemClickListener.onClick(v, getAdapterPosition());
                    }
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mItemClickListener != null) {
                        mItemClickListener.onLongClick(v, getAdapterPosition());
                    }
                    return false;
                }
            });

            remoteDeviceDetailsConnectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDeviceConnectionListener != null) {
                        DeviceInfo deviceInfo = mDataSet.get(getAdapterPosition());
                        mDeviceConnectionListener.onDeviceConnection(deviceInfo);
                    }
                }
            });
        }
    }

    // View Holder for "undo" state item
    public class ViewHolderUndo extends RecyclerView.ViewHolder {

        public View layout;
        public TextView removedTextView;
        public Button undoButton;

        public ViewHolderUndo(View v) {
            super(v);
            layout = v;
            removedTextView = (TextView) v.findViewById(R.id.recycler_item_removed_text_view);
            undoButton = (Button) v.findViewById(R.id.recycler_item_undo_btn);
        }
    }


    // Provide suitable constructor depending on the kind of data set
    public RemoteDevicesRecyclerAdapter(Context context) {

        this.mContext = context;
        mDataSet = new SortedList<DeviceInfo>(DeviceInfo.class, sortedListCallback);
    }

    // Create new view (view holder) (using layout inflater)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case VIEW_TYPE_NORMAL: {
                View v = inflater.inflate(R.layout.recycler_item_remote_device, parent, false);
                // set the view's size, margins, paddings and layout params
                viewHolder = new ViewHolderNormal(v);
                break;
            }
            case VIEW_TYPE_UNDO: {
                View v = inflater.inflate(R.layout.recycler_item_undo, parent, false);
                // set the view's size, margins, paddings and layout params
                viewHolder = new ViewHolderUndo(v);
                break;
            }
            default:
                throw new IllegalArgumentException("View type is incorrect!");
        }

        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    @TargetApi(16)
    public int getItemViewType(int position) {

        DeviceInfo deviceInfo = mDataSet.get(position);

        if (pendingRemovalItems.contains(deviceInfo)) {
            return VIEW_TYPE_UNDO;
        }

        return VIEW_TYPE_NORMAL;
    }

    // bind data to the view
    @Override
    @TargetApi(16)
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        DeviceInfo deviceInfo = mDataSet.get(position);

        if (pendingRemovalItems.contains(deviceInfo)) {
            // UNDO STATE of item
            bindViewHolderUndo( (ViewHolderUndo) viewHolder, position);
        } else {
            // NORMAL STATE of item
            bindViewHolderNormal( (ViewHolderNormal) viewHolder, position);
        }
    }

    private void bindViewHolderNormal(ViewHolderNormal viewHolder, int position) {

        DeviceInfo deviceInfo = mDataSet.get(position);

        viewHolder.remoteDeviceTextView.setText(deviceInfo.getDeviceName());

        if(deviceInfo.getType() == DeviceInfo.Type.REMOTELY_FOUND) {
            // remote devices (bonjour detected)
            viewHolder.remoteDeviceImageView.setImageResource(R.drawable.ic_remote_devices_white_24dp);

        } else if(deviceInfo.getType() == DeviceInfo.Type.USER_DEFINED) {
            // user defined devices
            viewHolder.remoteDeviceImageView.setImageResource(R.drawable.ic_user_devices_black_24dp);
        }

        viewHolder.remoteDeviceDetailsNameTextView
                .setText(mContext.getString(R.string.remote_device_details_name)
                        + ": " + deviceInfo.getDeviceName());
        viewHolder.remoteDeviceDetailsHostTextView
                .setText(mContext.getString(R.string.remote_device_details_host)
                        + ": " + deviceInfo.getHost());
        viewHolder.remoteDeviceDetailsPortNumberTextView
                .setText(mContext.getString(R.string.remote_device_details_port_number)
                        + ": " + deviceInfo.getPortNumber());

        if( undoItemsOpen == 0 && unwindItemPosition == position) {
            viewHolder.remoteDeviceDetailsView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.remoteDeviceDetailsView.setVisibility(View.GONE);
        }
    }

    private void bindViewHolderUndo(ViewHolderUndo viewHolder, int position) {

        final DeviceInfo deviceInfo = mDataSet.get(position);

        viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user wants to undo the removal
                Runnable pendingRemovalRunnable = pendingRemovalRunnables.get(deviceInfo);
                pendingRemovalRunnables.remove(deviceInfo);
                if(pendingRemovalRunnable != null) {
                    handler.removeCallbacks(pendingRemovalRunnable);
                }
                pendingRemovalItems.remove(deviceInfo);

                // rebind item in "normal" state
                synchronized(this) {
                    undoItemsOpen--;
                }
                notifyItemChanged(mDataSet.indexOf(deviceInfo));
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if( unwindItemPosition > -1) {
                    int oldUnwindItemPosition = unwindItemPosition;
                    unwindItemPosition = -1; // to make items collapsed
                    notifyItemChanged(oldUnwindItemPosition);
                }
            }
        });

    }

    /**
     * SortedList interface
     **/

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

    public int indexOf(DeviceInfo item) {
        return mDataSet.indexOf(item);
    }

    public void updateItemAt(int position, DeviceInfo item) {
        mDataSet.updateItemAt(position, item);
        // manually notify about dataset change as
        // there may be update of the device info with
        // the same name but different host and port number
        notifyItemChanged(position);
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
        unwindItemPosition = -1;
        return mDataSet.remove(item);
    }

    public DeviceInfo removeItemAt(int position) {
        unwindItemPosition = -1;
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

    public void unwindItemAtPosition(int position) {
        int oldUnwindItemPosition = unwindItemPosition;
        unwindItemPosition = position;


        if(oldUnwindItemPosition >=0) {
            notifyItemChanged(oldUnwindItemPosition);
        }

        notifyItemChanged(unwindItemPosition);

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

    /**
     * SwipeDeleteHelper.AdapterInterface implementation
     */
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3 [sec]

    private List<DeviceInfo> pendingRemovalItems = new ArrayList<>();
    private Map<DeviceInfo, Runnable> pendingRemovalRunnables = new HashMap<>();
    private Handler handler = new Handler(); // handler for running delayed runnables

    @Override
    public boolean isUndoOn() {
        return true;
    }

    @Override
    public boolean isItemRemovable(int position) {

        DeviceInfo item = mDataSet.get(position);
        if(item.getType() == DeviceInfo.Type.REMOTELY_FOUND){
            return false;
        }

        return true;
    }

    @Override
    public boolean isPendingRemoval(int position) {

        DeviceInfo item = mDataSet.get(position);
        return pendingRemovalItems.contains(item);
    }

    @Override
    public void pendingRemoval(int position) {

        final DeviceInfo item = mDataSet.get(position);
        if(!pendingRemovalItems.contains(item)) {
            pendingRemovalItems.add(item);
            synchronized (this) {
                undoItemsOpen++;
            }
            // notify RecyclerView to redraw item in "undo" state
            notifyItemChanged(position);
            // create, store and post a Runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        undoItemsOpen--;
                    }
                    remove(mDataSet.indexOf(item));
                }
            };
            pendingRemovalRunnables.put(item, pendingRemovalRunnable);
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
        }
    }

    @Override
    public void remove(int position) {
        DeviceInfo item = mDataSet.get(position);
        if(pendingRemovalItems.contains(item)) {
            pendingRemovalItems.remove(item);
        }
        remove(item);

        mContext.getContentResolver()
                .delete(Uri.withAppendedPath(UserDeviceInfoProvider.CONTENT_URI,
                        String.valueOf(((UserDeviceInfo) item).getDeviceId()) ), null, null);
    }
}
