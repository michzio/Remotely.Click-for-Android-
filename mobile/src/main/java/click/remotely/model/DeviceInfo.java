package click.remotely.model;

/**
 * Created by michzio on 05/08/2017.
 */

public class DeviceInfo {

    enum Type {
        REMOTELY_FOUND,
        USER_DEFINED
    }

    private String deviceName;
    private String host;
    private Integer portNumber;

    protected DeviceInfo.Type type;

    public DeviceInfo(String deviceName, String host, Integer portNumber) {
        this.deviceName = deviceName;
        this.host = host;
        this.portNumber = portNumber;
    }

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return  deviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceInfo)) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (!deviceName.equals(that.deviceName)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = deviceName.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
