package hr.ferit.lifequalityapp.ui.measurements.automatic

data class AutomaticMeasurement(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var noiseAmplitude: Int = 0,
    var temperature: Float? = null,
    var relativeHumidity: Float? = null,
    var pressure: Float? = null,
)

