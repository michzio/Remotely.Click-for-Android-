package click.remotely.android;

import android.database.Cursor;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

import click.remotely.android.dialog.UserDeviceDialogFragment;
import click.remotely.database.UserDeviceInfoProvider;
import click.remotely.model.DeviceInfo;
import click.remotely.model.RemoteDeviceInfo;
import click.remotely.model.RemoteDevicesRecyclerAdapter;
import click.remotely.model.UserDeviceInfo;
import click.remotely.networking.NSDHelper;

public class RemoteDevicesActivity extends AppCompatActivity {

    private static final String TAG = RemoteDevicesActivity.class.getName();
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
    private Boolean mIsLargeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate method called.");

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

        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
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
        mRecyclerAdapter = new RemoteDevicesRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume method called.");

        if(mNsdHelper != null) {
            mNsdHelper.discoverNetworkServices(SERVICE_TYPE);
        }
        getSupportLoaderManager().restartLoader(USER_DEVICES_LOADER, null, userDevicesLoader);
    }

    @Override
    protected void onPause() {

        Log.d(TAG, "onPause method called");

        if(mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        //getSupportLoaderManager().destroyLoader(USER_DEVICES_LOADER);
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "onDestroy method called");
        if(mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        getSupportLoaderManager().destroyLoader(USER_DEVICES_LOADER);
        super.onDestroy();
    }

    // remote device found/lost listeners implementation
    NSDHelper.OnNetworkServicesMapChangedListener remoteDevicesMapListener = new NSDHelper.OnNetworkServicesMapChangedListener() {


        @Override
        public void onNetworkServiceAdded(Map<String, NsdServiceInfo> networkServicesMap, String serviceName, NsdServiceInfo serviceInfo) {

            Log.d(TAG,  "Remote device " + serviceName + " added.");
            // wrap NsdServiceInfo object into RemoteDeviceInfo object
            DeviceInfo deviceInfo = new RemoteDeviceInfo(serviceInfo);
            mRecyclerAdapter.add(deviceInfo);
        }

        @Override
        public void onNetworkServiceRemoved(Map<String, NsdServiceInfo> networkServicesMap, String serviceName, NsdServiceInfo oldServiceInfo) {

            Log.d(TAG,  "Remote device " + serviceName + " removed.");
            // wrap NsdServiceInfo object into RemoteDeviceInfo object
            DeviceInfo deviceInfo = new RemoteDeviceInfo(oldServiceInfo);
            mRecyclerAdapter.remove(deviceInfo);
        }

        @Override
        public void onNetworkServicesCleared(Map<String, NsdServiceInfo> networkServicesMap) {

            Log.d(TAG,  "Remote devices cleared.");
            mRecyclerAdapter.clear();
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

                    DeviceInfo deviceInfo =  new UserDeviceInfo(userDeviceId, userDeviceName, userDeviceHost, userDevicePortNumber);

                    mRecyclerAdapter.add(deviceInfo);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

            if(loader.getId() == USER_DEVICES_LOADER) {

            }

        }
    };

    private static final String USER_DEVICE_DIALOG_FRAGMENT_TAG = "USER_DEVICE_DIALOG_FRAGMENT";

    private void showModalUserDeviceDialogFragment() {

        // close existing dialog fragments if any
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(USER_DEVICE_DIALOG_FRAGMENT_TAG);
        if(fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        UserDeviceDialogFragment userDeviceDialogFragment = UserDeviceDialogFragment.newInstance(getString(R.string.dialog_user_device_title));
        userDeviceDialogFragment.setOnDialogEventListener(userDeviceDialogListener);

        if(mIsLargeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            userDeviceDialogFragment.show(fragmentManager, USER_DEVICE_DIALOG_FRAGMENT_TAG);
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Transition animation
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            fragmentTransaction.add(android.R.id.content, userDeviceDialogFragment, USER_DEVICE_DIALOG_FRAGMENT_TAG)
                               .addToBackStack(USER_DEVICE_DIALOG_FRAGMENT_TAG)
                               .commit();
        }
    }


    UserDeviceDialogFragment.OnDialogEventListener userDeviceDialogListener = new UserDeviceDialogFragment.OnDialogEventListener() {
        @Override
        public void onDialogClose() {
            Log.d(TAG, "User Device Dialog closed!");
            handleUserDeviceDialogClose();
        }
    };

    @Override
    public void onBackPressed() {

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        if(backStackEntryCount > 0) {
            FragmentManager.BackStackEntry backStackEntry =
                    getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1);

            if(backStackEntry.getName().equals(USER_DEVICE_DIALOG_FRAGMENT_TAG)) {
                Log.d(TAG, "onBackPress found UserDeviceDialogFragment back stack entry.");
                handleUserDeviceDialogClose();
            }
        }

        /*
            if(getSupportFragmentManager()
                    .findFragmentByTag(USER_DEVICE_DIALOG_FRAGMENT_TAG) != null) {
                Log.d(TAG, "onBackPress found UserDeviceDialogFragment fragment.");
            }
         */
        super.onBackPressed();
    }

    private void handleUserDeviceDialogClose() {

        //getSupportLoaderManager().restartLoader(USER_DEVICES_LOADER, null, userDevicesLoader);
    }
}


