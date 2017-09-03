package click.remotely.networking;

/**
 * Created by michzio on 16/08/2017.
 */

public class ClientInfo {

    public static final String CLIENT_PORT_ANY = "0";
    public static final String CLIENT_OS = "Android";

    String pasvPort;            // port client sock binds
    String pasvIp;              // ip address of client host
    String connPort;            // port client is connecting to
    String connIp;              // ip address client is connecting to
    int sockFd;             // client socket file descriptor
    String securityPassword;    // password required to authenticate client on the server
    String clientIdentity;      // client identity to authenticate client on the server
    String clientOS;            // client operating system to authenticate client on the server

    // client events callbacks
    OnConnectionStartListener  connectionStartListener;
    OnConnectionAuthenticatedListener connectionAuthenticatedListener;
    OnConnectionEndListener connectionEndListener;
    OnConnectionErrorListener connectionErrorListener;
    OnDatagramErrorListener datagramErrorListener;

    // client events callbacks custom arguments
    Object connectionStartCallbackArg;
    Object connectionAuthenticatedCallbackArg;
    Object connectionEndCallbackArg;
    Object connectionErrorCallbackArg;
    Object datagramErrorCallbackArg;

    public interface OnConnectionStartListener {
        public void onConnectionStart(int sockFd, Object callbackArg);
    }

    public interface OnConnectionAuthenticatedListener {
        public void onConnectionAuthenticated(int sockFd, Object callbackArg);
    }

    public interface OnConnectionEndListener {
        public void onConnectionEnd(int sockFd, Object callbackArg);
    }

    public interface OnConnectionErrorListener {
        public void onConnectionError(int sockFd, int errorCode, String errorMsg, Object callbackArg);
    }

    public interface OnDatagramErrorListener {
        public void onDatagramError(int sockFd, int errorCode, String errorMsg, Object callbackArg);
    }

    public String getPasvPort() {
        return pasvPort;
    }

    public void setPasvPort(String pasvPort) {
        this.pasvPort = pasvPort;
    }

    public String getPasvIp() {
        return pasvIp;
    }

    public void setPasvIp(String pasvIp) {
        this.pasvIp = pasvIp;
    }

    public String getConnPort() {
        return connPort;
    }

    public void setConnPort(String connPort) {
        this.connPort = connPort;
    }

    public String getConnIp() {
        return connIp;
    }

    public void setConnIp(String connIp) {
        this.connIp = connIp;
    }

    public int getSockFd() {
        return sockFd;
    }

    public void setSockFd(int sockFd) {
        this.sockFd = sockFd;
    }

    public String getSecurityPassword() {
        return securityPassword;
    }

    public void setSecurityPassword(String securityPassword) {
        this.securityPassword = securityPassword;
    }

    public String getClientIdentity() {
        return clientIdentity;
    }

    public void setClientIdentity(String clientIdentity) {
        this.clientIdentity = clientIdentity;
    }

    public String getClientOS() {
        return clientOS;
    }

    public void setClientOS(String clientOS) {
        this.clientOS = clientOS;
    }

    public OnConnectionStartListener getConnectionStartListener() {
        return connectionStartListener;
    }

    public void setConnectionStartListener(OnConnectionStartListener connectionStartListener) {
        this.connectionStartListener = connectionStartListener;
    }

    public OnConnectionAuthenticatedListener getConnectionAuthenticatedListener() {
        return connectionAuthenticatedListener;
    }

    public void setConnectionAuthenticatedListener(OnConnectionAuthenticatedListener connectionAuthenticatedListener) {
        this.connectionAuthenticatedListener = connectionAuthenticatedListener;
    }

    public OnConnectionEndListener getConnectionEndListener() {
        return connectionEndListener;
    }

    public void setConnectionEndListener(OnConnectionEndListener connectionEndListener) {
        this.connectionEndListener = connectionEndListener;
    }

    public OnConnectionErrorListener getConnectionErrorListener() {
        return connectionErrorListener;
    }

    public void setConnectionErrorListener(OnConnectionErrorListener connectionErrorListener) {
        this.connectionErrorListener = connectionErrorListener;
    }

    public OnDatagramErrorListener getDatagramErrorListener() {
        return datagramErrorListener;
    }

    public void setDatagramErrorListener(OnDatagramErrorListener datagramErrorListener) {
        this.datagramErrorListener = datagramErrorListener;
    }

    public Object getConnectionStartCallbackArg() {
        return connectionStartCallbackArg;
    }

    public void setConnectionStartCallbackArg(Object connectionStartCallbackArg) {
        this.connectionStartCallbackArg = connectionStartCallbackArg;
    }

    public Object getConnectionAuthenticatedCallbackArg() {
        return connectionAuthenticatedCallbackArg;
    }

    public void setConnectionAuthenticatedCallbackArg(Object connectionAuthenticatedCallbackArg) {
        this.connectionAuthenticatedCallbackArg = connectionAuthenticatedCallbackArg;
    }

    public Object getConnectionEndCallbackArg() {
        return connectionEndCallbackArg;
    }

    public void setConnectionEndCallbackArg(Object connectionEndCallbackArg) {
        this.connectionEndCallbackArg = connectionEndCallbackArg;
    }

    public Object getConnectionErrorCallbackArg() {
        return connectionErrorCallbackArg;
    }

    public void setConnectionErrorCallbackArg(Object connectionErrorCallbackArg) {
        this.connectionErrorCallbackArg = connectionErrorCallbackArg;
    }

    public Object getDatagramErrorCallbackArg() {
        return datagramErrorCallbackArg;
    }

    public void setDatagramErrorCallbackArg(Object datagramErrorCallbackArg) {
        this.datagramErrorCallbackArg = datagramErrorCallbackArg;
    }
}

