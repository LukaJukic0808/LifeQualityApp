package hr.ferit.lifequalityapp.sensing

abstract class MeasurableSensor(
    protected val sensorType: Int,
) {
    protected var onSensorValuesChanged: ((FloatArray) -> Unit)? = null
    abstract val doesSensorExist: Boolean
    abstract fun startListening()
    abstract fun stopListening()
    fun setOnSensorValueChangedListener(listener: (FloatArray) -> Unit) {
        onSensorValuesChanged = listener
    }
}
