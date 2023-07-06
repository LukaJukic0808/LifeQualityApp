package hr.ferit.lifequalityapp.sensing.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import hr.ferit.lifequalityapp.sensing.AndroidSensor

class HumiditySensor(context: Context) :
    AndroidSensor(
        context = context,
        sensorFeature = PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY,
        sensorType = Sensor.TYPE_RELATIVE_HUMIDITY,
    )
