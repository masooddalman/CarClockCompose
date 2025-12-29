## THIS PROJECT IS STILL UNDER DEVELOPMENT

# Car Clock

A unique digital number display for Android built with Jetpack Compose. This project demonstrates a creative 7-segment display where the segments are formed by animated cars that drive from garages to their positions.

## Overview

This application showcases advanced animation techniques in Jetpack Compose, specifically using Bézier curves to create smooth, natural paths for UI elements. It turns a standard digital counter into a fun, animated traffic scene.

## Features

- **Animated Digits**: Numbers transition smoothly with cars driving into place to form the segments.
- **Bézier Curve Paths**: Cars travel along smooth cubic Bézier curves rather than straight lines, simulating natural driving movement.
- **Jetpack Compose**: Built entirely using the modern Android UI toolkit.
- **Smart Parking Logic**: specific logic determines which cars should "park" in garages and which should move to new segments to minimize unnecessary movement.
- **Interactive Demo**: Includes a manual counter to test digit transitions (0-9).

## Disclaimer

> [!NOTE]
> This project is for a **personal portfolio**. It is designed to demonstrate skills in Kotlin, Jetpack Compose, and custom animations.
> 

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material3)
- **Animation**: Compose Animation API (`Animatable`, `Tween`, `LaunchedEffect`)

## How it Works

1. **7-Segment Map**: The app maps each digit (0-9) to a standard 7-segment display layout.
2. **Car Assignment**: "Cars" are dynamic composables assigned to occupy these segments.
3. **Path Calculation**: When the number updates, the app calculates a start and end point. It then generates a Bézier curve (using control points) to create a smooth path for the car to follow.
4. **Garages**: If a car is not needed for a number (e.g., the number '1' only needs 2 segments), the extra cars drive to off-screen "garage" positions.

## Getting Started

1. Clone this repository.
2. Open the project in **Android Studio**.
3. Sync Gradle project.
4. Run the application on an Android Emulator or physical device.
