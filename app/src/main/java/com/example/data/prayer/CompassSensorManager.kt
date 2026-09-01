package com.example.data.prayer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Provides real-time heading (azimuth in degrees: 0° = North, 90° = East, 180° = South, 270° = West)
 * using the device's hardware rotation vector and geomagnetic sensors.
 */
class CompassSensorManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val rotationVectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val hasCompassSensors: Boolean
        get() = rotationVectorSensor != null || (accelerometerSensor != null && magnetometerSensor != null)

    fun getHeadingFlow(): Flow<Float> = callbackFlow {
        if (sensorManager == null) {
            trySend(0f)
            close()
            return@callbackFlow
        }

        var smoothedHeading = 0f
        var hasInitializedHeading = false

        val listener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientationAngles = FloatArray(3)
            private val truncatedRotationVector = FloatArray(4)

            private var lastAccelerometer = FloatArray(3)
            private var lastMagnetometer = FloatArray(3)
            private var lastAccelerometerSet = false
            private var lastMagnetometerSet = false

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                var rawHeading: Float? = null

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val vec = if (event.values.size > 4) {
                        System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4)
                        truncatedRotationVector
                    } else {
                        event.values
                    }

                    SensorManager.getRotationMatrixFromVector(rotationMatrix, vec)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val rad = orientationAngles[0]
                    rawHeading = (Math.toDegrees(rad.toDouble()).toFloat() + 360f) % 360f
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                    lastAccelerometerSet = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                    lastMagnetometerSet = true
                }

                if (rawHeading == null && lastAccelerometerSet && lastMagnetometerSet) {
                    val success = SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        lastAccelerometer,
                        lastMagnetometer
                    )
                    if (success) {
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val rad = orientationAngles[0]
                        rawHeading = (Math.toDegrees(rad.toDouble()).toFloat() + 360f) % 360f
                    }
                }

                rawHeading?.let { heading ->
                    // Smooth filtering for natural compass rotation without jitter
                    if (!hasInitializedHeading) {
                        smoothedHeading = heading
                        hasInitializedHeading = true
                    } else {
                        // Angle diff accounting for 0/360 wrap-around
                        var diff = (heading - smoothedHeading + 540f) % 360f - 180f
                        smoothedHeading = (smoothedHeading + diff * 0.25f + 360f) % 360f
                    }
                    trySend(smoothedHeading)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No-op
            }
        }

        // Register sensors
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                listener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        } else {
            if (accelerometerSensor != null) {
                sensorManager.registerListener(
                    listener,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
            if (magnetometerSensor != null) {
                sensorManager.registerListener(
                    listener,
                    magnetometerSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
