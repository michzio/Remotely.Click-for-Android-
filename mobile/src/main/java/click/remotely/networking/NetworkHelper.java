package click.remotely.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.util.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import click.remotely.common.Bytes;

/**
 * Created by michzio on 13/05/2017.
 */

public class NetworkHelper {

    private static final String TAG = NetworkHelper.class.getName();

    private Context context;

    private NetworkHelper(Context context) {

        this.context = context;
    }

    public static NetworkHelper with(Context context) {
        return new NetworkHelper(context);
    }

    public String getLocalSubnetMask() {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        return intToIpAddress(dhcpInfo.netmask);
    }

    public String getLocalIpAddress() throws UnknownHostException {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        return intToIpAddress(wifiInfo.getIpAddress());

        /*
         * Another method to decode IP address from integer value
         *  byte[] localIpBytes =  BigInteger.valueOf(wifiInfo.getIpAddress()).toByteArray();
         *  List<Byte> localIpBytesList = Arrays.asList(Bytes.toObjectArray(localIpBytes));
         *  Collections.reverse(localIpBytesList);
         *
         *  String localIpAddress = InetAddress.getByAddress( Bytes.getPrimitiveArray(localIpBytesList) ).getHostAddress();
         *
         *  return  localIpAddress;
        */
    }

    public List<String> getIpAddressesFromArpCache() {

        List<String> ipAddresses = new ArrayList<>();

        try {
            Scanner scanner = new Scanner( new FileInputStream("/proc/net/arp") );
            if(scanner.hasNextLine()) scanner.nextLine(); // skip header
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] cols = line.split("\\s+");
                if (cols != null && cols.length >= 4) {
                    String mac = cols[3];
                    if(mac.indexOf("00:00:00:00:00:00") < 0) {
                        // Log.d(TAG, "+" + mac + "+");
                        // Log.d(TAG, line);
                        ipAddresses.add(cols[0]);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return ipAddresses;
    }

    private String intToIpAddress(int i) {

        return  (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
    }


    public static class NetworkDeviceScannerTask extends AsyncTask<Void, String, Map<String,String>> {

        private static final String TAG = NetworkDeviceScannerTask.class.getName();

        private WeakReference<Context> mContext;
        private WeakReference<Callback> mCallback;

        public NetworkDeviceScannerTask(Context context, Callback callback) {
            mContext = new WeakReference<Context>(context);
            mCallback = new WeakReference<Callback>(callback);
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            Log.d(TAG, "Scanning local network for devices...");

            Map<String, String> hostNames = new HashMap<>();

            // dereferencing context object
            Context context = mContext.get();

            if(context != null) {

                try {
                    String localIpAddress = NetworkHelper.with(context).getLocalIpAddress();
                    String localSubnetMask = NetworkHelper.with(context).getLocalSubnetMask();

                    SubnetUtils  utils = new SubnetUtils(localIpAddress, localSubnetMask);
                    String[] allIps = utils.getInfo().getAllAddresses();

                    for(int i=0; i<allIps.length; i++) {

                        // Log.d(TAG, allIps[i]);

                        InetAddress address = InetAddress.getByName(allIps[i]);
                        boolean reachable = address.isReachable(1000);
                        String hostName = address.getCanonicalHostName();

                        if (reachable) {
                            // Log.i(TAG, "Host: " + hostName + "(" + allIps[i] + ") is reachable!");
                            hostNames.put(allIps[i], hostName);
                            publishProgress(allIps[i], hostName);
                        }
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return hostNames;
        }

        protected void onProgressUpdate(String... newHostFound) {

                if(mCallback.get() != null)
                    mCallback.get().onNetworkScannerNewHost(newHostFound[1], newHostFound[0]);
        }


        protected void onPostExecute(HashMap<String, String> hostNames) {

            if(mCallback.get() != null)
                mCallback.get().onNetworkScannerCompleted(hostNames);
        }


        public interface Callback {

            void onNetworkScannerCompleted(HashMap<String,String> hostNames);
            void onNetworkScannerNewHost(String hostName, String ipAddress);

        }
    }
}
