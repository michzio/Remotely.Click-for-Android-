package click.remotely.networking;

/**
 * Created by michzio on 10/08/2017.
 */

public class RemoteControllerClient {

    private static final String TAG = RemoteControllerClient.class.getName();

    // load 'native-lib' library
    static {
        System.loadLibrary("native-lib");
    }

    // native methods
    public native void connect(ClientInfo clientInfo);
    public native void disconnect(ClientInfo clientInfo);
    public native void sendMessage(ClientInfo clientInfo, String message);

}
