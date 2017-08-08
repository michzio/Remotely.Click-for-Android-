package click.remotely.networking;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by michzio on 15/05/2017.
 */

public class NSDHelper {

    public interface OnNetworkServicesMapChangedListener {
        void onNetworkServiceAdded(Map<String, NsdServiceInfo> networkServicesMap, String serviceName, NsdServiceInfo serviceInfo);
        void onNetworkServiceRemoved(Map<String, NsdServiceInfo> networkServicesMap, String serviceName, NsdServiceInfo oldServiceInfo);
        void onNetworkServicesCleared(Map<String, NsdServiceInfo> networkServicesMap);
        void onNetworkServicesMapChanged(Map<String, NsdServiceInfo> networkServicesMap);
    }

    private OnNetworkServicesMapChangedListener mapChangedListener;

    private static final String TAG = NSDHelper.class.getName();

    private Context mContext;
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mService;

    // initialized while registering new network service
    private String mRegisteredServiceName;
    // initialized before discovering given network service
    private String mDiscoverableServiceName;
    private String mDiscoverableServiceType;

    private boolean mIsListening = false;

    // collection of found network services
    private Map<String, NsdServiceInfo> mNetworkServicesMap;

    private NSDHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        mNetworkServicesMap = new LinkedHashMap<>();
    }

    public static NSDHelper with(Context context) {
        return new NSDHelper(context);
    }

    public Map<String, NsdServiceInfo> getNetworkServicesMap() {
        return mNetworkServicesMap;
    }

    // simplified helper listeners
    public void setOnNetworkServicesMapChangedListener(OnNetworkServicesMapChangedListener listener) {

        this.mapChangedListener = listener;
    }

    // complex native listeners
    public NSDHelper addRegistrationListener(NsdManager.RegistrationListener listener) {
        mRegistrationListener = listener;
        return this;
    }

    @TargetApi(16)
    public NSDHelper addDefaultRegistrationListener() {

        mRegistrationListener = new NsdManager.RegistrationListener() {


            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mRegisteredServiceName = serviceInfo.getServiceName();
                Log.d(TAG, "Network Service Registered: " + mRegisteredServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.e(TAG, "Network Service Registration failed!");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d(TAG, "Network Service Unregistered: " + mRegisteredServiceName);
                mRegisteredServiceName = null;
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
                Log.e(TAG, "Network Service Unregistration failes!");
            }
        };

        return this;
    }

    public NSDHelper addDisoveryListener(NsdManager.DiscoveryListener listener) {
        mDiscoveryListener = listener;
        return this;
    }

    @TargetApi(16)
    public NSDHelper addDefaultDisoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "Service discovery started");
            }

            //  Called as soon as service discovery begins.
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Service discovery fails");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed with error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }


            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Discovery stopped: " + serviceType);
                // Clear service list
                mNetworkServicesMap.clear();
                // Notify service list changed
                if(mapChangedListener != null) {
                    mapChangedListener.onNetworkServicesCleared(mNetworkServicesMap);
                    mapChangedListener.onNetworkServicesMapChanged(mNetworkServicesMap);
                }
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {

                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery succeed: " + serviceInfo);

                if (!serviceInfo.getServiceType().equals(mDiscoverableServiceType)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + serviceInfo.getServiceType());
                }  else if (mRegisteredServiceName != null && serviceInfo.getServiceName().equals(mRegisteredServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + mRegisteredServiceName);
                } else if (mDiscoverableServiceName == null || serviceInfo.getServiceName().contains(mDiscoverableServiceName)){
                    // Add new service to list
                   mNetworkServicesMap.put(serviceInfo.getServiceName(), serviceInfo);
                    // Notify service has been added to list
                    if(mapChangedListener != null) {
                        mapChangedListener.onNetworkServiceAdded(mNetworkServicesMap, serviceInfo.getServiceName(), serviceInfo);
                        mapChangedListener.onNetworkServicesMapChanged(mNetworkServicesMap);
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "Service lost: " + serviceInfo);
                // Remove service from list
                NsdServiceInfo oldServiceInfo = mNetworkServicesMap.remove(serviceInfo.getServiceName());
                // Notify service has been removed from list
                if (mapChangedListener != null) {
                    mapChangedListener.onNetworkServiceRemoved(mNetworkServicesMap, serviceInfo.getServiceName(), oldServiceInfo);
                    mapChangedListener.onNetworkServicesMapChanged(mNetworkServicesMap);
                }
            }
        };

        return this;
    }

    public NSDHelper addResolveListener(NsdManager.ResolveListener listener) {
        mResolveListener = listener;
        return this;
    }

    @TargetApi(16)
    public NSDHelper addDefaultResolveListener() {

        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Resolve succeeded: " + serviceInfo);

                if(mRegisteredServiceName != null && serviceInfo.getServiceName().equals(mRegisteredServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                InetAddress host = mService.getHost();
                Log.d(TAG, "Resolved host: " + host + ", " + port + ".");
            }
        };

        return this;
    }

    @TargetApi(16)
    public NSDHelper registerNetworkService(String serviceName, String serviceType, int port) {

        if(mRegistrationListener != null) {
            // Create the NsdServiceInfo object, and populate it.
            NsdServiceInfo serviceInfo = new NsdServiceInfo();
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceInfo.setServiceName(serviceName);
            serviceInfo.setServiceType(serviceType);
            serviceInfo.setPort(port);

            mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
            mIsListening = true;
        } else {
            Log.e(TAG, "Registration Listener not specified. Couldn't register network service.");
        }

        return this;
    }

    @TargetApi(16)
    public NSDHelper discoverNetworkServices(String serviceType) {
        return discoverNetworkServices(serviceType, null);
    }

    @TargetApi(16)
    public NSDHelper discoverNetworkServices(String serviceType, String serviceName) {

        if(mDiscoveryListener != null) {
            mDiscoverableServiceType = serviceType;
            mDiscoverableServiceName = serviceName;
            mNsdManager.discoverServices(
                    serviceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            mIsListening = true;
        } else {
            Log.e(TAG, "Discovery Listener not specified! Couldn't start network service discovery.");
        }

        return this;
    }

    @TargetApi(16)
    public NSDHelper resolveNetworkService(NsdServiceInfo serviceInfo) {

        if(mResolveListener != null) {
            mNsdManager.resolveService(serviceInfo, mResolveListener);
        } else {
            Log.e(TAG, "Resolve Listener not specified! Couldn't resolve network service.");
        }

        return this;
    }

    @TargetApi(16)
    public void tearDown() {

        if(mIsListening) {

            if (mRegistrationListener != null) {
                mNsdManager.unregisterService(mRegistrationListener);
            }
            if (mDiscoveryListener != null) {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }

            mIsListening = false;
        }
    }
}
