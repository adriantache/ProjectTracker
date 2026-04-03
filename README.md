# Project Tracker

A personal project management application built with modern Android development practices. This app helps track projects, tasks, and
categories with local and remote synchronization.

## 🚀 Features

- **Project Management**: Create and organize projects.
- **Task Tracking**: Break down projects into manageable tasks.
- **Categorization**: Group projects by categories for better organization.
- **Dashboard**: A comprehensive overview of your progress, including:
  - **Active & Completed Projects**: Track your overall project status.
  - **Task Statistics**: View total and completed tasks, and monitor your weekly pace.
  - **Recent Projects**: Quick access to your most recently updated work.
  - **Category Overview**: Browse and manage projects by their respective categories.
- **Local Persistence**: Full offline support using Room database.
- **Cloud Sync**: Remote storage and authentication powered by Firebase.
- **Modern UI**: Built entirely with Jetpack Compose and Material 3.
- **Secure Auth**: Google Sign-In integration via the Credentials API.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Backend
  **: [Firebase Realtime Database](https://firebase.google.com/docs/database) & [Firebase Auth](https://firebase.google.com/docs/auth)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Asynchronous Programming
  **: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Image/Animation**: [Lottie](https://airbnb.io/lottie/#/android)
- **Testing**: JUnit, MockK, Turbine, and Espresso

## 🏗 Architecture

The project follows **[Android Actual Clean Architecture](https://adriantache.com/architecture)**, a custom architectural pattern inspired
by Clean Architecture. It focuses on a clear separation of concerns:

- **Domain Layer**: Pure Kotlin layer containing Business Logic, Entities, and Repository Interfaces.
- **Data Layer**: Implementation of Repository Interfaces, managing data from Room (local) and Firebase (remote).
- **UI Layer**: Modern UI implementation using Jetpack Compose and ViewModels.

## 📦 Project Structure

```text
app/src/main/java/com/adriantache/projecttracker/
├── data/       # Data sources, Entities, Mappers, and Repository implementations
├── di/         # Hilt Modules
├── domain/     # Use Cases, Domain Models, and Repository interfaces
├── ui/         # Compose UI, ViewModels, and Theme
└── MainActivity.kt
```

## ⚙️ Setup

1. Clone the repository.
2. (Optional) Configure signing properties in `local.properties` for release builds.
3. Build and run!

---
Developed by [Adrian Tache](https://github.com/adriantache)
