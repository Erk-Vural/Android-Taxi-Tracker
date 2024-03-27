# Taxi Tracker

Taxi Tracker is an Android application developed using Kotlin and the Google Maps API to display data stored in Firebase Realtime Database. The app allows users to store and retrieve taxi location data in real-time and visualize it on Google Maps. Additionally, it utilizes the Directions API to draw routes between destinations.

## Features

- **Real-time Location Tracking**: Store and retrieve taxi location data from Firebase Realtime Database.
- **Google Maps Integration**: Display taxi locations on Google Maps for real-time visualization.
- **Route Drawing**: Utilize the Directions API to draw routes between taxi destinations.

## Technologies Used

- Kotlin
- Firebase Realtime Database
- Google Maps API
- Directions API

## Installation

1. Clone the repository:
   ```bash
    git clone https://github.com/your_username/taxi-tracker.git
   ```

2. Open the project in Android Studio.

3. Connect the project to Firebase:
   - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
   - Add your Android app to the Firebase project and follow the setup instructions.
   - Download the `google-services.json` file and place it in the `app` directory of your Android project.

4. Enable the necessary APIs in the [Google Cloud Console](https://console.cloud.google.com/):
   - Google Maps Android API
   - Directions API

5. Run the app on an Android emulator or a physical device.

## Usage

1. Sign in to the app using your credentials.
2. Allow location permissions if prompted.
3. View taxi locations displayed on the map.
4. Tap on a taxi marker to view details or draw routes between destinations.
