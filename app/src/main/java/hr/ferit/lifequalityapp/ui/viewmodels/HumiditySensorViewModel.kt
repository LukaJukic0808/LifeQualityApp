package hr.ferit.lifequalityapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import hr.ferit.lifequalityapp.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class HumiditySensorViewModel (private val humiditySensor: MeasurableSensor) : ViewModel() {

    var relativeHumidity = MutableStateFlow(0.0f)
    val doesSensorExist = humiditySensor.doesSensorExist

    init{
        humiditySensor.startListening()
        humiditySensor.setOnSensorValueChangedListener { values ->
            relativeHumidity.value = values[0]
        }
    }

    override fun onCleared() {
        super.onCleared()
        humiditySensor.stopListening()
    }
}