package click.remotely.model;

import android.annotation.TargetApi;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;

/**
 * Created by michzio on 05/08/2017.
 */

public class RemoteDeviceInfo extends DeviceInfo {

    private NsdServiceInfo serviceInfo;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public RemoteDeviceInfo(NsdServiceInfo serviceInfo) {

        super(  serviceInfo.getServiceName(),
                serviceInfo.getHost() != null ? serviceInfo.getHost().getHostAddress() : null,
                serviceInfo.getPort()   );

        this.serviceInfo = serviceInfo;
        this.type = Type.REMOTELY_FOUND;
    }

    public NsdServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(NsdServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
