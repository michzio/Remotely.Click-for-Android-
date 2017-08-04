package click.remotely.model;

/**
 * Created by michzio on 02/08/2017.
 */

public class UserDeviceInfo {

    private Integer deviceId;
    private String deviceName;
    private String host;
    private Integer portNumber;

    public UserDeviceInfo(Integer deviceId, String deviceName, String host, Integer portNumber) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.host = host;
        this.portNumber = portNumber;
    }

    public Integer getDeviceId() { return deviceId; }

    public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
