# ServiceIn

An **Android-based mobile application** that provides **motorcycle home service** for maintenance and repair. Built with **Clean Architecture** to separate concerns, keeping the codebase clean, scalable, and easy to maintain.

## Features

- Customer & workshop registration and login
- Motorcycle home service booking
- Location selection with maps
- Real-time chat between customers and workshops
- Digital wallet for transactions
- Workshop rating & reviews
- Real-time order status notifications

## Getting Started

### Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/rifqifajarr/ServiceIn.git
   cd ServiceIn
   ```

2. Open the project in Android Studio.

3. Add your google-services.json file from Firebase inside the app/ directory.

4. Run the app on an emulator or Android device.

## Project Structure

```
src/
├── data/ # Repository, Data Sources (Firestore, Preferences, etc.)
├── domain/ # Entities & Use Cases
├── ui/ # View, ViewModel, State
├── di/ # Dependency Injection (Hilt)
└── core/ # Utilities, resources, base classes
```
