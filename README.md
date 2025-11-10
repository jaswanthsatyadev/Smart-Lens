# SmartLens - Intelligent Product Scanner

**Scan Smarter, Choose Better**

SmartLens is an AI-powered Android app that helps you make healthier choices by scanning product barcodes to get instant health scores, nutritional information, and personalized warnings.

## Features

- 🔍 **Barcode Scanner** - Scan any food or beauty product
- 📊 **Health Scoring** - AI-powered health analysis
- 🌍 **Multi-API Data** - OpenFoodFacts + USDA FoodData Central integration
- 🏆 **NutriScore** - Official nutritional grade display
- 🔎 **Product Search** - Find products by name
- ➕ **Add Products** - Contribute missing products to the database

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt/Dagger
- **Networking**: Retrofit + OkHttp
- **JSON**: Moshi
- **Image Loading**: Coil
- **Camera**: CameraX
- **Barcode Scanning**: ML Kit
- **Backend**: Firebase (Auth, Firestore, Storage)
- **APIs**: OpenFoodFacts, USDA FoodData Central

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK 24+

### 1. Clone the Repository
```bash
git clone https://github.com/jaswanthsatyadev/Smart-Lens.git
cd Smart-Lens
```

### 2. Configure API Keys

Copy `local.properties.example` to `local.properties`:
```bash
cp local.properties.example local.properties
```

Edit `local.properties` and add your USDA API key:
```properties
USDA_API_KEY=your_actual_api_key_here
```

Get a free USDA API key from: https://fdc.nal.usda.gov/api-key-signup.html



### 3. Build and Run

```bash
./gradlew assembleDebug
```

Or open the project in Android Studio and click Run.

## Project Structure

```
app/src/main/java/com/evolvarc/smartlens/
├── data/              # Data layer
│   ├── remote/        # API services
│   ├── repository/    # Repository implementations
│   └── mapper/        # Data mappers
├── domain/            # Domain layer
│   ├── model/         # Domain models
│   └── usecase/       # Business logic
├── ui/                # Presentation layer
│   ├── scanner/       # Barcode scanner
│   ├── product/       # Product details
│   ├── search/        # Product search
│   ├── profile/       # User profile
│   └── components/    # Reusable UI components
└── di/                # Dependency injection
```

## Release Build

To build a release APK:

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/SmartLens-v1.0.0-release.apk`

## ProGuard

ProGuard is enabled for release builds with optimized rules for:
- Firebase
- Retrofit & OkHttp
- Moshi
- Hilt/Dagger
- CameraX
- ML Kit
- Jetpack Compose

## Developer

**Jaswanth Satya Dev**
- LinkedIn: [linkedin.com/in/jaswanth-satya-dev](https://www.linkedin.com/in/jaswanth-satya-dev/)
- GitHub: [github.com/jaswanthsatyadev](https://github.com/jaswanthsatyadev)
- X (Twitter): [x.com/jaswanthsatydev](https://x.com/jaswanthsatydev)
- Email: jaswanthsatyadev@gmail.com

## License

This project is distributed under a custom license. See the top-level `LICENSE` file for full details.

Permission is granted to:
- Use, copy, modify, and contribute to this software
- Use for personal, educational, or non-commercial purposes

Commercial use requires explicit written permission.

## Acknowledgments

- OpenFoodFacts for product database
- USDA FoodData Central for nutritional information
- Firebase for backend services
- Material Design for UI components
