# KarFlex

<p align="center">
  <img src="docs/karflex-logo.png" alt="KarFlex logo" width="120" />
</p>

<p align="center"><strong>Simple vehicle rental for every trip.</strong></p>

<p align="center">
  <a href="https://github.com/KarFlex-MTS-LAB/KarFlex/actions/workflows/android-build-release.yml"><img src="https://github.com/KarFlex-MTS-LAB/KarFlex/actions/workflows/android-build-release.yml/badge.svg" alt="Android build status" /></a>
</p>

KarFlex is an Android vehicle-rental application for browsing available vehicles, renting a vehicle, managing active rentals, and finding the KarFlex store location.

## Features

- Username and password registration with validation
- Four-digit OTP authentication sent by SMS on every login
- Vehicle catalog loaded from JSON and cached locally
- Vehicle detail pages with rental duration validation
- Active rental updates, cancellation, and automatic completion status
- Rental history with vehicle-name search
- Profile, logout, and KarFlex store location on Google Maps

## Tech Stack

- Kotlin and Android SDK 37
- AndroidX AppCompat, Activity KTX, ConstraintLayout, and View Binding
- Room Database over SQLite
- RecyclerView and Material Components
- Volley for vehicle JSON retrieval
- Google Maps SDK for Android
- SMS Manager and SMS Retriever API for OTP flow

## Project Structure

```text
app/src/main/java/com/example/karflex/
├── data/
│   ├── client/       Retrofit configuration
│   ├── dao/          Room DAOs and API contract
│   ├── entities/     User, vehicle, and rental entities
│   └── repository/   Vehicle and rental data access
├── utils/            Image and SMS helpers
├── *Activity.kt      Authentication and detail screens
└── *Fragment.kt      Browse, rental, and profile sections
```

## Requirements

- Android Studio
- Android SDK Platform 37
- JDK 17 or newer
- An Android emulator or a physical Android device

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/KarFlex-MTS-LAB/KarFlex.git
   cd KarFlex
   ```

2. Open the project in Android Studio and allow Gradle to sync.
3. Select an emulator or connected Android device.
4. Run the `app` configuration.

Build a debug APK from the terminal:

```powershell
.\gradlew.bat :app:assembleDebug
```

Install the debug APK on a connected device:

```powershell
.\gradlew.bat :app:installDebug
```

## Data Source

The vehicle catalog is retrieved through Volley from the course JSON endpoint and then stored in the local Room/SQLite database:

<https://gist.githubusercontent.com/axelkrnwn/01c506ac60735f553af951e550634b59/raw/08489b2ff76e1356a6df38d09948716628024195/karflex.json>

## Store Location

The map marker is configured for the KarFlex store location at latitude `-6.201733` and longitude `106.781592`.

## Notes

- The app requests SMS permission so it can send the login OTP from the device.
- Google Maps functionality requires a valid Maps SDK API key in the Android application configuration.
- Local vehicle image fallbacks are bundled in `app/src/main/res/drawable/` so the catalog remains usable when remote image hosts are unavailable.

## License

This project is maintained for the KarFlex Mobile Technology Solution project.
