package click.remotely.android;

import android.database.Cursor;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

import click.remotely.database.UserDeviceInfoProvider;
import click.remotely.model.RemoteDevicesRecyclerAdapter;
import click.remotely.model.UserDeviceInfo;
import click.remotely.networking.NSDHelper;

public class RemoteDevicesActivity extends AppCompatActivity {

    private static final int USER_DEVICES_LOADER = 0x02;

    public static final String SERVICE_NAME = "Remotely.Click";
    public static final String SERVICE_TYPE = "_remotely_click._tcp.";

    private NSDHelper mNsdHelper;
    private Map<String, UserDeviceInfo> mUserDevicesMap;

    private RecyclerView mRecyclerView;
    private RemoteDevicesRecyclerAdapter mRecyclerAdapter; // custom RecyclerView.Adapter
    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;

    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // configure activity's view
        setContentView(R.layout.activity_remote_devices);

        configureToolbar();
        configureFloatingActionButton();
        configureNetworkServiceDiscovery();
        configureUserDevicesMap();
        configureRecyclerView();
    }

    private void configureToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void configureFloatingActionButton() {

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.remote_devices_fab_add_custom_device);
        mFloatingActionButton.setOnClickListener(v -> showModalUserDeviceDialogFragment());
    }

    private void configureNetworkServiceDiscovery() {
        // configure Network Service Discovery using helper
        mNsdHelper = NSDHelper.with(this)
                // discover any service name of given service type
                .addDefaultDisoveryListener()
                .addDefaultResolveListener();

        mNsdHelper.setOnNetworkServicesMapChangedListener(remoteDevicesMapListener);
    }

    private void configureUserDevicesMap() {
        // configure User Devices map
        mUserDevicesMap = new LinkedHashMap<>();
        getSupportLoaderManager().initLoader(USER_DEVICES_LOADER, null, userDevicesLoader);
    }

    // This function need to have NSD and UserDevicesMap configured previously
    private void configureRecyclerView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_remote_devices);
        // to improve performance if you know that
        // changes in content do not change the layout
        // size of RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);
        mRecyclerAdapter = new RemoteDevicesRecyclerAdapter(mNsdHelper.getNetworkServicesMap(), mUserDevicesMap);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mNsdHelper != null) {
            mNsdHelper.discoverNetworkServices(SERVICE_TYPE);
        }
        getSupportLoaderManager().restartLoader(USER_DEVICES_LOADER, null, userDevicesLoader);
    }

    @Override
    protected void onPause() {

        if(mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        getSupportLoaderManager().destroyLoader(USER_DEVICES_LOADER);
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if(mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        super.onDestroy();
    }

    // remote device found/lost listeners implementation
    NSDHelper.OnNetworkServicesMapChangedListener remoteDevicesMapListener = new NSDHelper.OnNetworkServicesMapChangedListener() {


        @Override
        public void onNetworkServiceAdded(Map<String, NsdServiceInfo> networkServicesMap, String serviceName, NsdServiceInfo serviceInfo) {

            mRecyclerAdapter.notifyRemoteDeviceAdded(serviceName);
        }

        @Override
        public void onNetworkServiceRemoved(Map<String, NsdServiceInfo> networkServicesMap, String serviceName, NsdServiceInfo oldServiceInfo) {

            mRecyclerAdapter.notifyRemoteDeviceRemoved(serviceName);
        }

        @Override
        public void onNetworkServicesCleared(Map<String, NsdServiceInfo> networkServicesMap) {

            mRecyclerAdapter.notifyRemoteDevicesCleared();
        }

        @Override
        public void onNetworkServicesMapChanged(Map<String, NsdServiceInfo> networkServicesMap) {

        }
    };

    // user defined devices loader implementation (from SQLite database)
    LoaderManager.LoaderCallbacks<Cursor> userDevicesLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        /**
         * This method is used to create CursorLoader after the initLoader() call
         * @id - to differentiate between Loaders that have to be created
         * @args - is a Bundle that contains additional arguments for constructing Loaders
         */
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            CursorLoader cursorLoader = null;

            switch (id) {

                case USER_DEVICES_LOADER:

                    // CursorLoader is used to construct the new query
                    String[] projection = {
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_USER_DEVICE_INFO_ID,
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_DEVICE_NAME,
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_HOST,
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_PORT_NUMBER
                    };

                    String where = null;
                    String[] whereArgs = null;
                    String sortOrder = null;
                    Uri queryUri = UserDeviceInfoProvider.CONTENT_URI;

                    // create and return the new CursorLoader
                    cursorLoader = new CursorLoader(RemoteDevicesActivity.this, queryUri, projection, where, whereArgs, sortOrder);

                    break;
                default:
                    throw new IllegalArgumentException("Wrong Loader identifier: " + id + "!");
            }
            return cursorLoader;
        }

        /**
         * Method called when new data are loaded from data source
         */
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            if(mUserDevicesMap.size() > 0) {
                mUserDevicesMap.clear();
                mRecyclerAdapter.notifyUserDevicesCleared();
            }

            if(cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    // retrieve next UserDeviceInfo item
                    Integer userDeviceId = cursor.getInt(cursor.getColumnIndexOrThrow(
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_USER_DEVICE_INFO_ID));
                    String userDeviceName = cursor.getString(cursor.getColumnIndexOrThrow(
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_DEVICE_NAME));
                    String userDeviceHost = cursor.getString(cursor.getColumnIndexOrThrow(
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_HOST));
                    Integer userDevicePortNumber = cursor.getInt(cursor.getColumnIndexOrThrow(
                            UserDeviceInfoProvider.UserDeviceInfoTable.COLUMN_PORT_NUMBER));

                    mUserDevicesMap.put(userDeviceName,
                            new UserDeviceInfo(userDeviceId, userDeviceName, userDeviceHost, userDevicePortNumber));

                    mRecyclerAdapter.notifyUserDeviceAdded(userDeviceName);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    private void showModalUserDeviceDialogFragment() {

        // close existing dialog fragments if any
        FragmentManager fragmentManager = getSupportFragmentManager();
    }
}


