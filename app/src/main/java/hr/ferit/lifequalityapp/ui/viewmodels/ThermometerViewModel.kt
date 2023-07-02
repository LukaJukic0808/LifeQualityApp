package hr.ferit.lifequalityapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import hr.ferit.lifequalityapp.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class ThermometerViewModel(private val thermometer: MeasurableSensor) : ViewModel() {

    var temperature = MutableStateFlow(0.0f)
    val doesSensorExist = thermometer.doesSensorExist

    init {
        thermometer.startListening()
        thermometer.setOnSensorValueChangedListener { values ->
            temperature.value = values[0]
        }
    }

    override fun onCleared() {
        super.onCleared()
        thermometer.stopListening()
    }
}