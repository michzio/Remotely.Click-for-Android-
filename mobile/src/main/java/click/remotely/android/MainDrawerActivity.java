package click.remotely.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import click.remotely.android.fragments.TouchpadFragment;
import click.remotely.android.interfaces.RemoteControllerClientInterface;
import click.remotely.android.services.RemoteControllerClientService;
import click.remotely.model.SoftwareKeyboard;

/**
 * Created by michzio on 15/07/2017.
 */

public class MainDrawerActivity extends DrawerActivity implements RemoteControllerClientInterface {

    private static final String TAG = MainDrawerActivity.class.getName();

    private Boolean mClientServiceBound = false;
    private RemoteControllerClientService mClientService = null;

    @Override
    public RemoteControllerClientService getClientService() {
        return mClientService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            displayInitialFragment();
        }
    }

    private void displayInitialFragment() {

        TouchpadFragment fragment = new TouchpadFragment();
        String fragmentName = getString(R.string.action_touchpad);

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();

            NavigationView navigationView = (NavigationView) findViewById(R.id.start_nav_view);
            navigationView.setCheckedItem(R.id.nav_touchpad);

            setTitle(R.string.action_touchpad);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart method called.");

        if(RemoteControllerClientService.isRunning(this)) {

            // bind to Remote Controller Client Service
            Intent intent = new Intent(this, RemoteControllerClientService.class);
            bindService(intent, mClientServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {

        Log.d(TAG, "onStop method called.");

        // unbind from Remote Controller Client Service
        if(mClientServiceBound) {
            unbindService(mClientServiceConnection);
            mClientServiceBound = false;
        }

        super.onStop();
    }

    private ServiceConnection mClientServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            // we've bound to Remote Controller Client Service, cast the IBinder and get service instance
            mClientService = ((RemoteControllerClientService.LocalBinder) binder).getService();
            mClientServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mClientServiceBound = false;
            mClientService = null;
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        Log.d(TAG, "Key code touched: " + keyCode);
        Log.d(TAG, "Key code touched (event): " + event.getKeyCode());
        Log.d(TAG, "Action touched: " + event.getAction());
        Log.d(TAG, "Unicode char: " + event.getUnicodeChar());
        Log.d(TAG, "Scan code: "+ event.getScanCode());

        String virtualKeyName = SoftwareKeyboard.keyCodeKeyNameMap.get(keyCode);
        if(virtualKeyName == null) {
            return super.onKeyUp(keyCode, event);
        }

        String modifierFlags = SoftwareKeyboard.getModifierFlags(event);

        RemoteControllerClientService clientService = getClientService();
        if(clientService != null) {
            clientService.keyboardInput(virtualKeyName, modifierFlags);
        }

        return true;
    }

}
