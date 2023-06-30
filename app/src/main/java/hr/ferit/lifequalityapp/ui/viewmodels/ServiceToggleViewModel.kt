package hr.ferit.lifequalityapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class ServiceToggleViewModel: ViewModel() {

    var isServiceRunning by mutableStateOf(false)

    init {
        isMyServiceRunning()
    }

    fun toggleService(){
        isServiceRunning = !isServiceRunning
    }
    private fun isMyServiceRunning() {
        isServiceRunning = false
    }
}

