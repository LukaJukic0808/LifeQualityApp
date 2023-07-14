package hr.ferit.lifequalityapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import hr.ferit.lifequalityapp.sensing.models.BarometerModel
import hr.ferit.lifequalityapp.sensing.models.HumiditySensorModel
import hr.ferit.lifequalityapp.sensing.models.ThermometerModel
import hr.ferit.lifequalityapp.ui.permissions.isMeasurementRunning
import hr.ferit.lifequalityapp.ui.screen.MainScreen
import hr.ferit.lifequalityapp.ui.theme.LifeQualityAppTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MainActivity : ComponentActivity(), KoinComponent {
    private val barometerModel: BarometerModel = get()
    private val humiditySensorModel: HumiditySensorModel = get()
    private val thermometerModel: ThermometerModel = get()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeQualityAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!applicationContext.isMeasurementRunning()) {
            barometerModel.start()
            humiditySensorModel.start()
            thermometerModel.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!applicationContext.isMeasurementRunning()) {
            barometerModel.stop()
            humiditySensorModel.stop()
            thermometerModel.stop()
        }
    }
}
