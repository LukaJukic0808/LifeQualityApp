package hr.ferit.lifequalityapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RadioButtonViewModel : ViewModel() {

    var selectedButton by mutableStateOf(0)

    init {
        selectButton(0)
    }

    fun selectButton(index: Int) {
        selectedButton = index
    }
}
