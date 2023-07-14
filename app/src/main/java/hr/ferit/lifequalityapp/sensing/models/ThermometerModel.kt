package hr.ferit.lifequalityapp.sensing.models

import hr.ferit.lifequalityapp.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class ThermometerModel(private val thermometer: MeasurableSensor) {

    var temperature = MutableStateFlow(0.0f)
    val doesSensorExist = thermometer.doesSensorExist

    fun start() {
        thermometer.startListening()
        thermometer.setOnSensorValueChangedListener { values ->
            temperature.value = values[0]
        }
    }

    fun stop() {
        thermometer.stopListening()
    }
}
