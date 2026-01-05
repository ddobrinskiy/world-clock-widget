# Distribution Guide - World Clock Widget

This document covers how to get the app onto your device and distribution options.

---

## 📦 Option 1: Direct APK Install (Simplest)

The fastest way to get the app on your device.

### Build the APK

```bash
# Debug build (for testing)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
cd /Users/dzlob/proj/personal/vc-android-world-clock
./gradlew assembleDebug
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk`

For a release build (smaller, optimized):

```bash
./gradlew assembleRelease
```

**Output:** `app/build/outputs/apk/release/app-release-unsigned.apk`

### Install via ADB (Device Connected)

```bash
# With USB debugging enabled on your device
export ANDROID_HOME="$HOME/Library/Android/sdk"
$ANDROID_HOME/platform-tools/adb install app/build/outputs/apk/debug/app-debug.apk
```

### Install via File Transfer

1. Copy the APK to your device (USB, email, cloud storage, etc.)
2. On your Android device:
   - Enable **Settings → Security → Unknown sources** (or per-app permission on newer Android)
   - Open a file manager and tap the APK
   - Follow the install prompts

---

## 📱 Option 2: GitHub Releases

Share with others via GitHub without going through an app store.

### Setup

1. Create a GitHub repository for this project
2. Build a signed release APK (see [Signing](#signing-your-apk) below)
3. Create a GitHub Release and attach the APK

### Benefits
- Version history
- Release notes
- Easy sharing via direct download links
- No approval process

---

## 🛒 Option 3: F-Droid (Recommended for FOSS)

F-Droid is a free and open-source app store. **Great fit for this app** since:
- ✅ MIT License (FOSS-compatible)
- ✅ No proprietary dependencies (no Firebase, Play Services)
- ✅ Privacy-friendly (no tracking, no ads)
- ✅ Works offline

### Requirements Checklist

| Requirement | Status |
|-------------|--------|
| Open source license (MIT, GPL, Apache, etc.) | ✅ MIT |
| Source code publicly accessible | ⬜ Need to publish repo |
| No proprietary libraries | ✅ Clean |
| No non-free network services | ✅ Works offline |
| Reproducible builds | ⬜ Need to verify |

### Preparation Steps

#### 1. Publish Source Code

Push to a public Git repository (GitLab, GitHub, Codeberg, etc.):

```bash
git remote add origin https://github.com/YOUR_USERNAME/world-clock-widget.git
git push -u origin main
```

#### 2. Add Fastlane Metadata

F-Droid uses Fastlane structure for app metadata. Create these files:

```
app/src/main/
└── fastlane/
    └── metadata/
        └── android/
            └── en-US/
                ├── title.txt           # App name (max 30 chars)
                ├── short_description.txt  # Max 80 chars
                ├── full_description.txt   # Full description
                ├── changelogs/
                │   └── 1.txt           # Changelog for version code 1
                └── images/
                    ├── icon.png        # 512x512 app icon
                    ├── phoneScreenshots/
                    │   ├── 1.png
                    │   └── 2.png
                    └── featureGraphic.png  # 1024x500 banner (optional)
```

**Example `title.txt`:**
```
World Clock Widget
```

**Example `short_description.txt`:**
```
Minimalist world clock with home screen widget. No ads, no tracking.
```

#### 3. Tag Releases

F-Droid builds from version tags:

```bash
git tag v1.0.0
git push origin v1.0.0
```

#### 4. Submit to F-Droid

1. Fork [fdroiddata](https://gitlab.com/fdroid/fdroiddata) on GitLab
2. Create a new branch
3. Add a metadata file: `metadata/com.worldclock.yml`

**Example `com.worldclock.yml`:**

```yaml
Categories:
  - Time
License: MIT
AuthorName: David
SourceCode: https://github.com/YOUR_USERNAME/world-clock-widget
IssueTracker: https://github.com/YOUR_USERNAME/world-clock-widget/issues

AutoName: World Clock Widget
Description: |
  A minimalistic Android app for tracking time across multiple timezones 
  with a beautiful home screen widget.

  Features:
  * Add multiple timezones from around the world
  * 24-hour format display
  * Resizable home screen widget
  * Material Design 3 with dark mode support
  * Battery efficient updates
  * No permissions, no ads, no tracking

RepoType: git
Repo: https://github.com/YOUR_USERNAME/world-clock-widget.git

Builds:
  - versionName: 1.0.0
    versionCode: 1
    commit: v1.0.0
    subdir: app
    gradle:
      - yes

AutoUpdateMode: Version
UpdateCheckMode: Tags
CurrentVersion: 1.0.0
CurrentVersionCode: 1
```

4. Submit a Merge Request to `fdroiddata`
5. Wait for review (can take 1-4 weeks)

### F-Droid Timeline

| Step | Time |
|------|------|
| Submit merge request | Day 1 |
| Initial review | 1-7 days |
| Build verification | 1-3 days |
| Merge & publish | Next index update (~weekly) |

---

## 🔐 Signing Your APK

For release builds and F-Droid, you need to sign your APK.

### Create a Keystore (One Time)

```bash
keytool -genkey -v -keystore world-clock-release.keystore \
  -alias worldclock \
  -keyalg RSA -keysize 2048 -validity 10000
```

**⚠️ Keep this keystore safe!** You'll need it for all future updates.

### Configure Signing in Gradle

Add to `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../world-clock-release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = "worldclock"
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### Build Signed APK

```bash
export KEYSTORE_PASSWORD="your_password"
export KEY_PASSWORD="your_password"
./gradlew assembleRelease
```

---

## 📊 Distribution Comparison

| Method | Effort | Reach | Auto-Updates | Trust |
|--------|--------|-------|--------------|-------|
| **APK (direct)** | ⭐ Lowest | Personal | ❌ | You |
| **GitHub Releases** | ⭐⭐ Low | Developers | ❌ | You |
| **F-Droid** | ⭐⭐⭐ Medium | FOSS community | ✅ | F-Droid |
| **Play Store** | ⭐⭐⭐⭐ High | Everyone | ✅ | Google |

---

## 🎯 Recommended Path

1. **Now:** Build debug APK → Install via ADB for personal use
2. **Soon:** Push to public GitHub repo
3. **Next:** Submit to F-Droid (fits the app's philosophy perfectly)

F-Droid is ideal because:
- Your app is FOSS (MIT license)
- No proprietary dependencies
- Privacy-first design
- Target audience likely uses F-Droid

---

## 🔗 Resources

- [F-Droid Submission Guide](https://f-droid.org/en/docs/Submitting_to_F-Droid_Quick_Start_Guide/)
- [F-Droid Inclusion Policy](https://f-droid.org/en/docs/Inclusion_Policy/)
- [Fastlane Metadata Structure](https://f-droid.org/en/docs/All_About_Descriptions_Graphics_and_Screenshots/)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)

