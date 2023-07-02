package hr.ferit.lifequalityapp.sensing.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import hr.ferit.lifequalityapp.sensing.AndroidSensor

class Barometer (context: Context):
    AndroidSensor(
        context = context,
        sensorFeature = PackageManager.FEATURE_SENSOR_BAROMETER,
        sensorType = Sensor.TYPE_PRESSURE,
    )