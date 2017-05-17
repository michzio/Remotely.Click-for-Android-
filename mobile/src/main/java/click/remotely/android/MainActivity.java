package click.remotely.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Network;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import click.remotely.networking.NSDHelper;
import click.remotely.networking.NetworkHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    NSDHelper nsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nsdHelper = NSDHelper.with(this) //.addRegistrationListener().registerNetworkService("Remotely.Click", "_remotelyclick._tcp.", 2017)
                .addDisoveryListener("", "_remotelyclick._tcp.").addResolveListener().discoverNetworkServices("_remotelyclick._tcp.");

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        try {
            String localIpAddress = NetworkHelper.with(this).getLocalIpAddress();
            String localSubnerMask = NetworkHelper.with(this).getLocalSubnetMask();
            Toast.makeText(this, "Local ip address is: " + localIpAddress, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Subnet mask is: " + localSubnerMask, Toast.LENGTH_LONG).show();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        new NetworkHelper.NetworkDeviceScannerTask(this, new NetworkHelper.NetworkDeviceScannerTask.Callback() {

            @Override
            public void onNetworkScannerCompleted(HashMap<String, String> hostNames) {

            }

            @Override
            public void onNetworkScannerNewHost(String hostName, String ipAddress) {
                Log.i(TAG, "Host: " + hostName + "(" + ipAddress + ") is reachable!");
            }
        }).execute();

        List<String> ipAddressesFromArp = NetworkHelper.with(this).getIpAddressesFromArpCache();
        for(String ipAddress : ipAddressesFromArp) {
            Log.d(TAG, "ip from arp cache: " + ipAddress);
        }
    }

    @Override
    protected void onDestroy() {
        nsdHelper.tearDown();
        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


}
