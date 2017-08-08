package click.remotely.model;

/**
 * Created by michzio on 02/08/2017.
 */

public class UserDeviceInfo extends DeviceInfo {

    private Integer deviceId;

    /**
     * Constructor used to create new UserDevice object
     * before inserting it to DB and generating auto incremented ID.
     **/
    public UserDeviceInfo(String deviceName, String host, Integer portNumber) {
        super(deviceName, host, portNumber);
        this.type = Type.USER_DEFINED;
    }

    public UserDeviceInfo(Integer deviceId, String deviceName, String host, Integer portNumber) {
        this(deviceName, host, portNumber);
        this.deviceId = deviceId;

    }

    public Integer getDeviceId() { return deviceId; }

    public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }
}
