# NyayaSetu Setup Guide

Complete setup and deployment guide for the NyayaSetu legal consultation platform.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Firebase Project Setup](#firebase-project-setup)
3. [Flutter Setup](#flutter-setup)
4. [Backend Setup](#backend-setup)
5. [Firestore Deployment](#firestore-deployment)
6. [Running the Application](#running-the-application)
7. [iOS App Store Deployment](#ios-app-store-deployment)
8. [Android Play Store Deployment](#android-play-store-deployment)

---

## Prerequisites

### Required Software

- **Flutter 3.x** (3.0.0 or higher)
  - Download from: https://flutter.dev/docs/get-started/install
  - Verify: `flutter --version`

- **Java 17** (JDK 17)
  - Download from: https://adoptium.net/
  - Verify: `java -version`

- **Maven 3.6+**
  - Download from: https://maven.apache.org/download.cgi
  - Verify: `mvn -version`

- **Firebase CLI**
  - Install: `npm install -g firebase-tools`
  - Verify: `firebase --version`

- **Android Studio** (for Android development)
  - Download from: https://developer.android.com/studio
  - Install Android SDK and platform tools

- **Xcode** (for iOS development, macOS only)
  - Download from Mac App Store
  - Install Xcode Command Line Tools: `xcode-select --install`

- **CocoaPods** (for iOS dependencies, macOS only)
  - Install: `sudo gem install cocoapods`

---

## Firebase Project Setup

### 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: `nyayasetu` (or your preferred name)
4. Enable or disable Google Analytics (optional)
5. Click "Create project"

### 2. Enable Authentication

1. In Firebase Console, go to **Build > Authentication**
2. Click "Get started"
3. Enable the following sign-in providers:
   - **Email/Password**: Enable and save
   - **Google**: Enable, set support email, and save

### 3. Enable Firestore Database

1. Go to **Build > Firestore Database**
2. Click "Create database"
3. Select "Start in production mode" (we'll deploy rules later)
4. Choose location: `asia-south1` (Mumbai) or nearest region
5. Click "Enable"

### 4. Generate Service Account JSON

1. Go to **Project settings > Service accounts**
2. Click "Generate new private key"
3. Download the JSON file
4. Rename it to `firebase-service-account.json`
5. Place it in the backend root: `/data/nyayasetu/backend/firebase-service-account.json`

**Important**: Never commit this file to version control. Add it to `.gitignore`.

### 5. Register Apps

#### Android App
1. Go to **Project settings > Your apps**
2. Click Android icon
3. Package name: `com.nyayasetu.app`
4. App nickname: `NyayaSetu`
5. Download `google-services.json`
6. Save for Flutter setup

#### iOS App
1. Click iOS+ icon
2. Bundle ID: `com.nyayasetu.app`
3. App nickname: `NyayaSetu`
4. Download `GoogleService-Info.plist`
5. Save for Flutter setup

---

## Flutter Setup

### 1. Navigate to Flutter Project

```bash
cd /data/nyayasetu/flutter_app
```

### 2. Install Flutter Dependencies

```bash
flutter pub get
```

### 3. Configure Firebase

#### Option A: Using FlutterFire CLI (Recommended)

1. Install FlutterFire CLI:
```bash
dart pub global activate flutterfire_cli
```

2. Login to Firebase:
```bash
firebase login
```

3. Configure Firebase for Flutter:
```bash
flutterfire configure
```

4. Select your Firebase project when prompted
5. Select platforms: `android`, `ios`
6. This generates `lib/firebase_options.dart`

#### Option B: Manual Configuration

1. Place `google-services.json` in:
```
flutter_app/android/app/google-services.json
```

2. Place `GoogleService-Info.plist` in:
```
flutter_app/ios/Runner/GoogleService-Info.plist
```

3. Update `lib/firebase_options.dart` with your project credentials (if not using FlutterFire CLI)

### 4. Update API Base URL

Edit `lib/core/constants.dart`:

```dart
static const String apiBaseUrl = 'https://your-backend-url.com/api';
```

For local development:
```dart
static const String apiBaseUrl = 'http://10.0.2.2:8080/api'; // Android emulator
// or
static const String apiBaseUrl = 'http://localhost:8080/api'; // iOS simulator
```

### 5. iOS Specific Setup

1. Navigate to iOS directory:
```bash
cd ios
pod install
cd ..
```

2. Update `ios/Runner/Info.plist` if needed for permissions

---

## Backend Setup

### 1. Navigate to Backend Directory

```bash
cd /data/nyayasetu/backend
```

### 2. Set Environment Variables

```bash
export FIREBASE_CREDENTIALS_PATH=./firebase-service-account.json
export CORS_ALLOWED_ORIGINS=http://localhost:3000,https://nyayasetu.web.app
```

Or create `.env` file (if using dotenv):
```properties
FIREBASE_CREDENTIALS_PATH=./firebase-service-account.json
CORS_ALLOWED_ORIGINS=*
```

### 3. Install Dependencies

```bash
mvn clean install
```

### 4. Run Backend Server

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### 5. Verify Backend

Test the health endpoint:
```bash
curl http://localhost:8080/api/advocates
```

Should return a list of advocates (or empty array).

---

## Firestore Deployment

### 1. Navigate to Firebase Directory

```bash
cd /data/nyayasetu/firebase
```

### 2. Login to Firebase

```bash
firebase login
```

### 3. Select Project

```bash
firebase use nyayasetu
```

Or initialize if not already configured:
```bash
firebase init firestore
```

### 4. Deploy Firestore Rules and Indexes

```bash
firebase deploy --only firestore
```

This deploys:
- `firestore.rules` - Security rules
- `firestore.indexes.json` - Composite indexes

### 5. Verify Deployment

Go to Firebase Console > Firestore Database > Rules to verify rules are deployed.

---

## Running the Application

### Development Mode

#### 1. Start Backend (Terminal 1)

```bash
cd /data/nyayasetu/backend
export FIREBASE_CREDENTIALS_PATH=./firebase-service-account.json
mvn spring-boot:run
```

#### 2. Start Flutter App (Terminal 2)

```bash
cd /data/nyayasetu/flutter_app

# For Android emulator
flutter run -d android

# For iOS simulator
flutter run -d ios

# For Chrome (web)
flutter run -d chrome
```

### Production Mode

#### Build APK (Android)

```bash
cd /data/nyayasetu/flutter_app
flutter build apk --release
```

APK location: `build/app/outputs/flutter-apk/app-release.apk`

#### Build App Bundle (Android - for Play Store)

```bash
flutter build appbundle --release
```

AAB location: `build/app/outputs/bundle/release/app-release.aab`

#### Build iOS (macOS only)

```bash
flutter build ios --release
```

---

## iOS App Store Deployment

### 1. Prerequisites

- Apple Developer Account ($99/year)
- macOS with Xcode installed
- Valid iOS distribution certificate
- App Store Connect account

### 2. Xcode Configuration

1. Open `ios/Runner.xcworkspace` in Xcode

2. Select **Runner** target

3. **General** tab:
   - Bundle Identifier: `com.nyayasetu.app`
   - Version: `1.0.0`
   - Build: `1`

4. **Signing & Capabilities** tab:
   - Enable "Automatically manage signing"
   - Select your team
   - Ensure signing certificate is valid

### 3. Archive the App

1. In Xcode menu: **Product > Archive**
2. Wait for archive to complete
3. Click "Distribute App"

### 4. Upload to App Store Connect

1. Select **App Store Connect**
2. Choose **Upload**
3. Follow prompts to upload

### 5. App Store Connect Configuration

1. Go to [App Store Connect](https://appstoreconnect.apple.com/)
2. Select your app
3. Fill in app information:
   - App name: NyayaSetu
   - Subtitle: Nyay ka Setu — The Bridge to Justice
   - Category: Business / Legal
   - Description: Legal consultation platform connecting users with verified lawyers
   - Keywords: legal, lawyer, consultation, india, justice
   - Support URL: [your support URL]
   - Privacy Policy URL: [your privacy policy URL]

4. Upload screenshots (6.7" and 6.5" iPhone required)

5. Select the uploaded build

6. Set pricing (Free or Paid)

### 6. Submit for Review

1. Click "Add for Review"
2. Answer export compliance questions
3. Submit for review

Review typically takes 24-48 hours.

### 7. TestFlight (Beta Testing)

1. In App Store Connect, go to **TestFlight** tab
2. Add internal testers (up to 100)
3. For external testers (up to 10,000):
   - Create a group
   - Add testers by email
   - Submit for beta review (usually faster than full review)

---

## Android Play Store Deployment

### 1. Prerequisites

- Google Play Developer Account ($25 one-time fee)
- App signing key (or use Play App Signing)

### 2. Create App in Play Console

1. Go to [Google Play Console](https://play.google.com/console/)
2. Click "Create app"
3. Fill in:
   - App name: NyayaSetu
   - Default language: English (India)
   - App or game: App
   - Free or paid: Free (or Paid)

### 3. Prepare Store Listing

1. **Short description**: Legal consultation platform connecting users with verified lawyers
2. **Full description**: Detailed description of features
3. **Graphics**:
   - App icon: 512x512 PNG
   - Feature graphic: 1024x500 PNG
   - Screenshots: At least 2 phone screenshots (16:9 or 9:16)

4. **Categorization**:
   - Category: Business
   - Tags: Legal, Consultation

### 4. Build Signed App Bundle

```bash
cd /data/nyayasetu/flutter_app

# Create keystore (if you don't have one)
keytool -genkey -v -keystore ~/nyayasetu-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias nyayasetu

# Update android/key.properties
storePassword=your_password
keyPassword=your_password
keyAlias=nyayasetu
storeFile=/path/to/nyayasetu-key.jks
```

Build the app bundle:
```bash
flutter build appbundle --release
```

### 5. Upload to Play Console

1. Go to **Production** > **Create new release**
2. Upload `app-release.aab` file
3. Wait for Play Store to process the bundle

### 6. Content Rating

1. Go to **Policy and programs > Content rating**
2. Complete the questionnaire
3. Save and submit

### 7. Pricing & Distribution

1. Go to **Monetize > Pricing & distribution**
2. Set pricing (Free or Paid)
3. Select distribution countries
4. Confirm compliance

### 8. Submit for Review

1. Go to **Production**
2. Click "Review release"
3. Click "Start rollout to production"

Review typically takes a few hours to 1-2 days.

### 9. Internal Testing (Optional)

1. Go to **Testing > Internal testing**
2. Create a release
3. Add testers by email
4. Testers receive email invitation

---

## Troubleshooting

### Backend Issues

**Firebase credentials error**:
- Verify `firebase-service-account.json` exists and is valid
- Check `FIREBASE_CREDENTIALS_PATH` environment variable

**CORS errors**:
- Update `CORS_ALLOWED_ORIGINS` in application.properties
- For development, use `*` (not recommended for production)

### Flutter Issues

**Firebase initialization error**:
- Ensure `google-services.json` is in `android/app/`
- Ensure `GoogleService-Info.plist` is in `ios/Runner/`
- Run `flutter clean` then `flutter pub get`

**API connection errors (Android emulator)**:
- Use `10.0.2.2` instead of `localhost` for Android emulator
- Example: `http://10.0.2.2:8080/api`

**iOS build errors**:
- Run `cd ios && pod install && cd ..`
- Clean Xcode: Product > Clean Build Folder

### Firestore Issues

**Permission denied errors**:
- Verify Firestore rules are deployed: `firebase deploy --only firestore:rules`
- Check Firebase Console for rule violations in logs

**Missing index errors**:
- Deploy indexes: `firebase deploy --only firestore:indexes`
- Or click the link in the error message to auto-create index

---

## Environment Variables Summary

### Backend (application.properties)

```properties
server.port=8080
firebase.credentials.path=${FIREBASE_CREDENTIALS_PATH:./firebase-service-account.json}
cors.allowed.origins=${CORS_ALLOWED_ORIGINS:*}
```

### Flutter (lib/core/constants.dart)

```dart
static const String apiBaseUrl = 'http://localhost:8080/api';
```

---

## Security Checklist

- [ ] Never commit `firebase-service-account.json` to version control
- [ ] Never commit `google-services.json` to public repositories
- [ ] Never commit `GoogleService-Info.plist` to public repositories
- [ ] Use environment variables for sensitive configuration
- [ ] Enable Firebase App Check in production
- [ ] Set up Firebase Auth domain restrictions
- [ ] Review and test Firestore security rules
- [ ] Use HTTPS for all API endpoints in production
- [ ] Enable ProGuard/R8 for Android release builds
- [ ] Enable code obfuscation for iOS release builds

---

## Support

For issues and questions:
- GitHub Issues: [your-repo-url]/issues
- Email: support@nyayasetu.com
- Documentation: [your-docs-url]

---

**Last Updated**: 2026-08-16
