package hr.ferit.lifequalityapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo

class ServiceToggleViewModel : ViewModel() {

    var isServiceRunning by mutableStateOf(false)

    fun isMyServiceRunning(workInfo: MutableList<WorkInfo>?) {
        if (workInfo.isNullOrEmpty()) {
            isServiceRunning = false
        } else {
            workInfo.forEach { info ->
                isServiceRunning = info.state == WorkInfo.State.ENQUEUED || info.state == WorkInfo.State.RUNNING
            }
        }
    }
}
