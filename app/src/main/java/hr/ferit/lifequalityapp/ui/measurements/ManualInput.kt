package hr.ferit.lifequalityapp.ui.measurements

data class ManualInput(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var noiseLevel: Int = 0,
    var temperature: Float? = null,
    var relativeHumidity: Float? = null,
    var pressure: Float? = null
)
