

# MQTT Real-Time Data Visualization App

This is an Android application that connects to an MQTT server, subscribes to specific topics, and visualizes the received data in real-time using a line chart. The application is designed for monitoring sensor data or similar IoT applications where data changes frequently and needs to be displayed graphically.

## Features

- **Real-time Data Visualization**: Displays incoming MQTT data on a line chart.
- **Multiple Topics Support**: Subscribes to multiple topics and shows each topic's data in a different color on the chart.
- **Smooth Graph Updates**: The graph updates in real-time without freezing the UI, thanks to optimized background processes.
- **Error Handling**: Displays errors using snackbars if the connection fails or an issue occurs.

## Technologies Used

- **Android Studio**: IDE for development.
- **Kotlin**: Programming language for Android.
- **MQTT (Message Queuing Telemetry Transport)**: A lightweight messaging protocol used for IoT applications.
- **MPAndroidChart**: A library for graphical representation of data on Android devices.
- **Kotlin Coroutines**: For handling asynchronous tasks like MQTT message handling and chart updates in the background.

## Getting Started

### Prerequisites

- **Android Studio** installed on your machine.
- **MQTT broker** (such as Mosquitto) running, and access to the MQTT server's IP and port.
- Basic knowledge of Kotlin and Android development.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/your-repository.git
   ```
   
2. Open the project in Android Studio.

3. Add the necessary dependencies to the `build.gradle` files. These include:
   ```groovy
   implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
   implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
   implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
   ```

4. Sync the project with the `build.gradle` files.

### Usage

1. Modify the MQTT server IP and port in the `MainActivity.kt` file:
   ```kotlin
   const val MQTT_SERVER = "your-mqtt-server-ip:port"
   ```
   
2. Set up your topics to subscribe to. You can modify the `MQTT_TOPIC` variables:
   ```kotlin
   const val MQTT_TOPIC = "topic1"
   const val MQTT_TOPIC2 = "topic2"
   const val MQTT_TOPIC3 = "topic3"
   ```

3. Build and run the application on an Android device or emulator.

4. The app will start subscribing to the defined MQTT topics and visualize the incoming data in real-time using line charts.

### Screenshots

Include screenshots or gifs of the application in action to demonstrate its functionality.

### APK

If you would like to test the application on your own device, you can download the APK from the following link:
- [Download APK](#)

## How it Works

The application connects to an MQTT server and subscribes to specific topics. As messages are received from these topics, the app processes the data and updates a line chart using the `MPAndroidChart` library. Each topic is represented by a different color in the chart, and the graph updates smoothly in real-time.

Key components include:
- **MQTT Connection**: The app uses the `org.eclipse.paho` MQTT client to connect to the broker and receive messages.
- **MPAndroidChart**: The data received from MQTT topics is displayed using a line chart, which updates dynamically as new data arrives.
- **Kotlin Coroutines**: The background operations (like MQTT message handling and data parsing) are done asynchronously using Kotlin Coroutines to prevent blocking the main UI thread.

## Dependencies

- **MPAndroidChart**: For rendering the line chart.
- **Eclipse Paho MQTT Client**: For MQTT message handling.

Add the following dependencies to your `build.gradle` file:
```groovy
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
```

## Future Improvements

- Add the ability to configure MQTT server settings directly from the app UI.
- Implement more customizable chart options (e.g., zooming, panning, etc.).
- Add the option to subscribe to additional topics dynamically.

## Contributing

If you'd like to contribute to this project:
1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Submit a pull request.


---

