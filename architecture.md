# Architecture: Android Actual Clean Architecture

This project follows **Android Actual Clean Architecture**, a refined version of Clean Architecture optimized for modern Android development
with Jetpack Compose.

## 📌 Core Principles

1. **Unidirectional Data Flow (UDF)**: State flows down from Use Cases to the UI; events flow up from the UI to Use Cases.
2. **State-Driven UI**: The UI is a passive reflection of the current State.
3. **Use Cases as State Managers**: Use Cases are not just "single action" classes. They manage the lifecycle of a specific feature's state,
   holding the `StateFlow` and coordinating business logic.
4. **Executable State**: The State object contains both the data to display and the lambdas (actions) the UI can trigger.
5. **Strict Layer Separation**:
    * **Domain**: Pure Kotlin. No Android dependencies. Contains Entities, Repository Interfaces, and Use Cases.
    * **Data**: Implementation of Repositories. Handles Room, Firebase, API calls, and Mappers.
    * **UI**: Jetpack Compose and ViewModels. ViewModels are "skinny," primarily acting as a bridge between the Domain's State and the UI.

---

## 🏗 Layers

### 1. Domain Layer (`com.adriantache.projecttracker.domain`)

The heart of the application.

- **Entities**: Plain Kotlin objects representing business data (e.g., `Project`, `Task`).
- **Repository Interfaces**: Define the contracts for data operations.
- **Use Cases**:
    * Maintain the UI state using `MutableStateFlow`.
    * Expose an immutable `StateFlow` to the UI.
    * Contain business logic and coordinate data flow between repositories.
    * *Example*: `CategoriesUseCase` manages the transition between Dashboard, Category, and Project views.

### 2. Data Layer (`com.adriantache.projecttracker.data`)

Responsible for providing data to the Domain layer.

- **Repository Implementations**: Concrete classes that decide whether to fetch data from local (Room) or remote (Firebase) sources.
- **Data Sources**:
    * **Local**: Room DAOs and Entities.
    * **Remote**: Firebase Realtime Database and Auth.
- **Mappers**: Extension functions to convert between Data Entities and Domain Entities.

### 3. UI Layer (`com.adriantache.projecttracker.ui`)

The presentation layer.

- **Compose Views**: Stateless composables that receive a `State` object and render the UI.
- **ViewModels**:
    * Inject Use Cases.
    * Expose the Use Case's `StateFlow`.
    * Survive configuration changes.
- **Navigation**: Uses Navigation Compose, driven by state changes in the Use Case.

---

## 🔄 State Management Flow

1. **Initialization**: The `ViewModel` is created and gets the `StateFlow` from the `UseCase`.
2. **Observation**: The Composable UI collects the `StateFlow` as a Compose State.
3. **Action**: The user interacts with the UI (e.g., clicks "Add Project").
4. **Event**: The UI calls a lambda provided in the current `State` object (e.g., `state.onAddProject(...)`).
5. **Logic**: The `UseCase` receives the call, performs business logic, updates the `Repository`, and updates the `MutableStateFlow`.
6. **Update**: The UI automatically recomposes with the new `State`.

---

## 🛠 Technology Stack

- **Dependency Injection**: Hilt
- **Concurrency**: Coroutines & Flow
- **Local Database**: Room
- **Cloud Backend**: Firebase (Auth & Realtime Database)
- **UI**: Jetpack Compose with Material 3
- **Testing**: JUnit, MockK, Turbine (for Flow testing)
