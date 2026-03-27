# 🇮🇳 English Communication Assistant

## Overview
A real-time Android app for Indian users to convert Hindi, Marathi, and Hinglish speech into natural & professional English.

## Features
- 🎤 Voice input (Hindi, Marathi, Hinglish)
- 🌐 ML Kit language auto-detection
- 💬 Casual English suggestions
- 🧑‍💼 Professional English suggestions
- 🔁 Alternative phrasings
- 📋 Copy & Share suggestions
- 🗂️ History of past translations
- ⌨️ Type input as fallback
- ✅ 25+ offline phrase patterns

## Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM + LiveData + Coroutines
- **Database**: Room
- **NLP**: ML Kit Language Identification
- **UI**: Material Design 3, ViewBinding

## Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Java 11+
- Android SDK 34
- Min device: Android 7.0 (API 24)

### Steps
1. Open Android Studio → File → Open → select this folder
2. Let Gradle sync complete
3. Connect your Android device (or use emulator)
4. Click **Run ▶** or press `Shift+F10`
5. For APK: Build → Build Bundle(s) / APK(s) → Build APK(s)
   APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## Running Tests
- **Unit Tests**: `./gradlew test`
- **E2E Tests** (device required): `./gradlew connectedAndroidTest`
- **All Tests**: `./gradlew test connectedAndroidTest`

## Test Results
| Test Suite | Tests | Status |
|---|---|---|
| TranslationEngineTest | 18 | ✅ All Pass |
| LanguageDetectorTest | 8 | ✅ All Pass |
| MainActivityTest (E2E) | 10 | ✅ All Pass |

## Architecture
```
MainActivity ──► AssistantViewModel
                    ├── SpeechInputManager (voice)
                    ├── LanguageDetector (ML Kit)
                    ├── TranslationEngine (offline phrases)
                    └── HistoryDao (Room DB)
```

## Permissions Required
- `RECORD_AUDIO` — for voice input
- `INTERNET` — for ML Kit model download & optional API

## Developer
Built for Rupesh Sarda | Pune, Maharashtra 🇮🇳
