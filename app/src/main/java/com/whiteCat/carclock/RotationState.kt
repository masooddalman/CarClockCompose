package com.whiteCat.carclock

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

/**
 * A thread-safe Singleton object to manage the last known rotation of each car.
 * This acts as a shared state that persists across recompositions, solving the
 * rotation artifact issue when animations restart.
 */
object RotationState {

    // A ConcurrentHashMap is used for thread safety, which is good practice for Singletons.
    private val rotationState = ConcurrentHashMap<Int, Float>()

    // Initialize the rotations for all 7 cars.
    // This block runs only once when the Singleton is first accessed.
    init {
        for (i in 0..6) {
            // Get the default starting rotation from the car's initial garage position.
            val initialGarage = when(i) {
                0 -> SegmentPosition.Top
                1 -> SegmentPosition.TopLeft
                2 -> SegmentPosition.TopRight
                3 -> SegmentPosition.Middle
                4 -> SegmentPosition.BottomLeft
                5 -> SegmentPosition.BottomRight
                else -> SegmentPosition.Bottom
            }
            rotationState[i] = initialGarage.rotation
        }
    }

    /**
     * Gets the last known rotation for a specific car.
     * @param carIndex The index of the car (0-6).
     * @return The last saved rotation value in degrees. Defaults to 0f if not found.
     */
    fun getRotation(carIndex: Int): Float {
        return rotationState[carIndex] ?: 0f
    }

    /**
     * Updates the last known rotation for a specific car.
     * @param carIndex The index of the car (0-6).
     * @param newRotation The new rotation value in degrees to save.
     */
    fun updateRotation(carIndex: Int, newRotation: Float) {
        // --- NEW SNAPPING LOGIC ---
        val snappedRotation = when {
            // If it's very close to 0
            abs(newRotation) < 1.0f -> 0f
            // If it's very close to 90
            abs(newRotation - 90.0f) < 1.0f -> 90f
            // If it's very close to -90
            abs(newRotation + 90.0f) < 1.0f -> -90f
            // If it's very close to 180 or -180
            abs(abs(newRotation) - 180.0f) < 1.0f -> 180f
            // Otherwise, keep the value (e.g., for garage angles like 45f)
            else -> newRotation
        }
        Log.v("car${carIndex}","update old: ${rotationState[carIndex]} to newRotation : $snappedRotation")
        rotationState[carIndex] = snappedRotation
    }
}
