package hr.ferit.lifequalityapp.sensing.models

import hr.ferit.lifequalityapp.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class BarometerModel(private val barometer: MeasurableSensor) {

    var pressure = MutableStateFlow(0.0f)
    val doesSensorExist = barometer.doesSensorExist

    fun start() {
        barometer.startListening()
        barometer.setOnSensorValueChangedListener { values ->
            pressure.value = values[0]
        }
    }

    fun stop() {
        barometer.stopListening()
    }
}
