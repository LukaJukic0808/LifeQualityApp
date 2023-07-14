package hr.ferit.lifequalityapp.sensing.models

import hr.ferit.lifequalityapp.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class HumiditySensorModel(private val humiditySensor: MeasurableSensor) {

    var relativeHumidity = MutableStateFlow(0.0f)
    val doesSensorExist = humiditySensor.doesSensorExist

    fun start() {
        humiditySensor.startListening()
        humiditySensor.setOnSensorValueChangedListener { values ->
            relativeHumidity.value = values[0]
        }
    }

    fun stop() {
        humiditySensor.stopListening()
    }
}
