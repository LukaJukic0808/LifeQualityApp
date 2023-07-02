package hr.ferit.lifequalityapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import hr.ferit.lifequalityapp.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class BarometerViewModel (private val barometer: MeasurableSensor) : ViewModel(){

    var pressure = MutableStateFlow(0.0f)
    val doesSensorExist = barometer.doesSensorExist

    init{
        barometer.startListening()
        barometer.setOnSensorValueChangedListener { values ->
            pressure.value = values[0]
        }
    }

    override fun onCleared() {
        super.onCleared()
        barometer.stopListening()
    }
}