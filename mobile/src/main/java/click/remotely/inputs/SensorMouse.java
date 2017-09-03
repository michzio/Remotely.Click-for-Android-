package click.remotely.inputs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import click.remotely.android.interfaces.RemoteControllerClientInterface;
import click.remotely.android.services.RemoteControllerClientService;

/**
 * Created by michzio on 29/08/2017.
 */

public class SensorMouse implements SensorEventListener {

    private static final String TAG = SensorMouse.class.getName();

    private Context mContext;

    private SensorManager mSensorManager;
    private Sensor mGravitySensor;
    private Sensor mGyroSensor;

    private float pitch = 0.0f /* x axis */, roll = 0.0f /* y axis */, azimuth = 0.0f /* z axis */;

    public SensorMouse(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void resumeSensorMouse() {

        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void pauseSensorMouse() {

        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

       if(event.sensor == mGyroSensor) {

           float axisX = event.values[0];
           float axisY = event.values[1];
           float axisZ = event.values[2];

           //Log.d(TAG, "Rotation vector: " + axisX + ", " + axisY + ", " + axisZ);

           RemoteControllerClientService clientService = ((RemoteControllerClientInterface) mContext).getClientService();
           if(clientService != null) {
               clientService.mouseMove(-axisZ*32, -axisX*32);
           }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
