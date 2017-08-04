package click.remotely.android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import android.widget.Toast

import java.net.UnknownHostException
import java.util.HashMap

import click.remotely.networking.NSDHelper
import click.remotely.networking.NetworkHelper

class MainActivity : AppCompatActivity() {

    internal var nsdHelper: NSDHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set Toolbar as ActionBar
        val toolbar = findViewById(R.id.toolbar) as Toolbar;
        setSupportActionBar(toolbar);

        nsdHelper = NSDHelper.with(this) //.addRegistrationListener().registerNetworkService("Remotely.Click", "_remotelyclick._tcp.", 2017)
                .addDefaultDisoveryListener().addDefaultResolveListener().discoverNetworkServices("_remotely_click._tcp.")

        // Example of a call to a native method
        val tv = findViewById(R.id.sample_text) as TextView
        tv.text = stringFromJNI()


        try {
            val localIpAddress = NetworkHelper.with(this).localIpAddress
            val localSubnerMask = NetworkHelper.with(this).localSubnetMask
            Toast.makeText(this, "Local ip address is: " + localIpAddress, Toast.LENGTH_LONG).show()
            Toast.makeText(this, "Subnet mask is: " + localSubnerMask, Toast.LENGTH_LONG).show()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }


        NetworkHelper.NetworkDeviceScannerTask(this, object : NetworkHelper.NetworkDeviceScannerTask.Callback {

            override fun onNetworkScannerCompleted(hostNames: HashMap<String, String>) {

            }

            override fun onNetworkScannerNewHost(hostName: String, ipAddress: String) {
                Log.i(TAG, "Host: $hostName($ipAddress) is reachable!")
            }
        }).execute()

        val ipAddressesFromArp = NetworkHelper.with(this).ipAddressesFromArpCache
        for (ipAddress in ipAddressesFromArp) {
            Log.d(TAG, "ip from arp cache: " + ipAddress)
        }
    }

    override fun onDestroy() {
        nsdHelper?.tearDown()
        super.onDestroy()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        private val TAG = MainActivity::class.java.name

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

}
