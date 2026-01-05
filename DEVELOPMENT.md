# Development Rules - World Clock Android App

This document outlines the development workflow for AI-assisted development on this project. The goal is to **minimize human input** while maintaining quality through systematic verification.

---

## 🔄 Core Development Loop: PLAN → IMPLEMENT → QA

Every change follows this cycle:

### 1. PLAN
- Clearly define what needs to be done
- Break down into small, atomic changes
- Identify which files need modification
- Consider potential side effects

### 2. IMPLEMENT
- Make **one change at a time**
- Build the project to verify compilation:
  ```bash
  export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
  cd /Users/dzlob/proj/personal/vc-android-world-clock
  ./gradlew assembleDebug
  ```
- Fix any build errors before proceeding

### 3. QA (Quality Assurance)
- Install the app on the emulator
- **Use computer-use MCP to visually verify** the change works
- Take screenshots to confirm UI changes
- Test the specific feature that was modified
- Only proceed to the next change after verification passes

---

## 📱 Self-Testing with Computer Use MCP

The AI should test changes **autonomously** using the Android emulator paired with computer-use MCP tools.

### Setup
1. Ensure the Android emulator is running
2. Use `adb` commands to install and control the app
3. Use computer-use MCP to take screenshots and verify UI

### Key Commands

**Check emulator status:**
```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
$ANDROID_HOME/platform-tools/adb devices
```

**Install the app:**
```bash
./gradlew installDebug
```

**Launch the app:**
```bash
$ANDROID_HOME/platform-tools/adb shell am start -n com.worldclock/.MainActivity
```

**Take screenshot (via adb):**
```bash
$ANDROID_HOME/platform-tools/adb shell screencap -p /sdcard/screenshot.png
$ANDROID_HOME/platform-tools/adb pull /sdcard/screenshot.png ~/Downloads/android_screenshot_$(date +%Y%m%d_%H%M%S).png
```

**View logs:**
```bash
$ANDROID_HOME/platform-tools/adb logcat -d | grep -i worldclock
```

### Visual Verification with Computer Use
- Use `mcp_computer-control_take_screenshot` to capture the emulator window
- Use `mcp_computer-control_click_screen` to interact with the app
- Verify that UI changes appear correctly
- Confirm that interactions work as expected

---

## 🎯 Iterative Implementation

**The cardinal rule: Try ONE thing, verify it works, then move on.**

### ✅ Good Practice
```
1. Add a new button → Build → Install → Screenshot → Verify button appears
2. Add click handler → Build → Install → Click button → Verify action works
3. Add persistence → Build → Install → Test save/load → Verify data persists
```

### ❌ Bad Practice
```
1. Add button + click handler + persistence + new screen all at once
2. Build (fails with 5 errors)
3. Spend time debugging interconnected issues
```

### Why Iterative?
- Easier to identify what broke
- Smaller blast radius for errors
- Faster debugging when issues occur
- More confidence in each step

---

## 🏗️ Build Verification

After every code change:

1. **Run the build:**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Check the result:**
   - `BUILD SUCCESSFUL` → Proceed to QA
   - `BUILD FAILED` → Fix errors before moving on

3. **Never stack changes on top of broken builds**

---

## 📋 Workflow Checklist

For each feature or fix:

- [ ] Define the change clearly
- [ ] Identify affected files
- [ ] Make the minimal code change
- [ ] Build successfully
- [ ] Install on emulator
- [ ] Use computer-use MCP to verify visually
- [ ] Test the specific functionality
- [ ] Confirm no regressions
- [ ] Move to next change

---

## 🛠️ Environment Setup

### Required Environment Variables
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
```

### Project Location
```
/Users/dzlob/proj/personal/vc-android-world-clock
```

### Key Paths
- **APK Output:** `app/build/outputs/apk/debug/app-debug.apk`
- **Main Source:** `app/src/main/java/com/worldclock/`
- **Resources:** `app/src/main/res/`
- **Manifest:** `app/src/main/AndroidManifest.xml`

---

## 🚫 What NOT to Do

1. **Don't make multiple unrelated changes at once**
2. **Don't skip the build step**
3. **Don't skip visual verification**
4. **Don't assume changes work without testing**
5. **Don't ask the user to test** — test it yourself with computer-use MCP
6. **Don't proceed with broken builds**

---

## ✅ What TO Do

1. **Make small, atomic changes**
2. **Build after every change**
3. **Test every change visually**
4. **Use screenshots to verify UI**
5. **Save all screenshots to `~/Downloads` with timestamps** (e.g., `screenshot_20260105_143052.png`)
6. **Check logs for errors**
7. **Document what was tested**
8. **Only proceed when current step is verified**

---

## 📸 Screenshot-Based Verification

**Important:** Always save screenshots to `~/Downloads` with timestamps in the filename (e.g., `android_screenshot_20260105_143052.png`).

When verifying UI changes:

1. Launch the app on emulator
2. Navigate to the affected screen
3. Take a screenshot using computer-use MCP (save to `~/Downloads`)
4. Visually confirm the change appears correctly
5. If the change involves interaction:
   - Click the element
   - Take another screenshot
   - Verify the expected result

---

## 🔧 Debugging Tips

### App Crashes
```bash
$ANDROID_HOME/platform-tools/adb logcat -d | grep -E "(FATAL|Exception|Error)" | tail -20
```

### UI Not Updating
- Force stop and restart the app:
  ```bash
  $ANDROID_HOME/platform-tools/adb shell am force-stop com.worldclock
  $ANDROID_HOME/platform-tools/adb shell am start -n com.worldclock/.MainActivity
  ```

### Build Cache Issues
```bash
./gradlew clean assembleDebug
```

---

## 🧪 Testing Examples

### Example 1: Testing UI Changes (Adding a Button)

```bash
# 1. Build and install
./gradlew assembleDebug
$ANDROID_HOME/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# 2. Launch the app
$ANDROID_HOME/platform-tools/adb shell am start -n com.worldclock/.MainActivity

# 3. Wait for app to load
sleep 2

# 4. Take screenshot to verify button appears
$ANDROID_HOME/platform-tools/adb shell screencap -p /sdcard/screenshot.png
$ANDROID_HOME/platform-tools/adb pull /sdcard/screenshot.png ~/Downloads/android_screenshot_$(date +%Y%m%d_%H%M%S).png
```

### Example 2: Testing Tap Interactions

```bash
# Tap at specific coordinates (x=350, y=500)
$ANDROID_HOME/platform-tools/adb shell input tap 350 500

# Take screenshot after tap
sleep 1
$ANDROID_HOME/platform-tools/adb shell screencap -p /sdcard/screenshot.png
$ANDROID_HOME/platform-tools/adb pull /sdcard/screenshot.png ~/Downloads/after_tap.png
```

### Example 3: Testing Drag-to-Reorder

```bash
# Long press and drag from (x1,y1) to (x2,y2) over 2 seconds
$ANDROID_HOME/platform-tools/adb shell input swipe 350 740 350 290 2000
```

### Example 4: Testing Swipe-to-Delete

```bash
# Swipe from right to left on an item
$ANDROID_HOME/platform-tools/adb shell input swipe 600 300 100 300 300
```

### Example 5: Testing Widget Updates

```bash
# Force widget update by opening the app
$ANDROID_HOME/platform-tools/adb shell am start -n com.worldclock/.MainActivity

# Go to home screen to see widget
$ANDROID_HOME/platform-tools/adb shell input keyevent KEYCODE_HOME
sleep 1

# Screenshot the home screen with widget
$ANDROID_HOME/platform-tools/adb shell screencap -p /sdcard/screenshot.png
$ANDROID_HOME/platform-tools/adb pull /sdcard/screenshot.png ~/Downloads/widget_screenshot.png
```

### Example 6: Waiting and Verifying Time Updates

```bash
# Check current time, wait, then verify widget updated
echo "Time before: $(date)"
sleep 60
echo "Time after: $(date)"

$ANDROID_HOME/platform-tools/adb shell screencap -p /sdcard/screenshot.png
$ANDROID_HOME/platform-tools/adb pull /sdcard/screenshot.png ~/Downloads/time_check.png
```

---

## 💻 Command Reference

### App Lifecycle

```bash
# Install app
$ANDROID_HOME/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app
$ANDROID_HOME/platform-tools/adb shell am start -n com.worldclock/.MainActivity

# Force stop app
$ANDROID_HOME/platform-tools/adb shell am force-stop com.worldclock

# Uninstall app
$ANDROID_HOME/platform-tools/adb uninstall com.worldclock

# Clear app data
$ANDROID_HOME/platform-tools/adb shell pm clear com.worldclock
```

### Input Events

```bash
# Tap at coordinates
$ANDROID_HOME/platform-tools/adb shell input tap <x> <y>

# Swipe (duration in ms)
$ANDROID_HOME/platform-tools/adb shell input swipe <x1> <y1> <x2> <y2> <duration_ms>

# Press back button
$ANDROID_HOME/platform-tools/adb shell input keyevent KEYCODE_BACK

# Press home button
$ANDROID_HOME/platform-tools/adb shell input keyevent KEYCODE_HOME

# Type text
$ANDROID_HOME/platform-tools/adb shell input text "hello"
```

### Debugging

```bash
# View all logs
$ANDROID_HOME/platform-tools/adb logcat

# Filter by app
$ANDROID_HOME/platform-tools/adb logcat | grep -i worldclock

# View crash logs
$ANDROID_HOME/platform-tools/adb logcat -d | grep -E "(FATAL|Exception|Error)" | tail -30

# Clear logcat buffer
$ANDROID_HOME/platform-tools/adb logcat -c
```

### Device Info

```bash
# List connected devices
$ANDROID_HOME/platform-tools/adb devices

# Get screen resolution
$ANDROID_HOME/platform-tools/adb shell wm size

# Get screen density
$ANDROID_HOME/platform-tools/adb shell wm density
```

---

## 🏛️ Architecture Decisions

### 1. Jetpack Compose for UI

**Decision:** Use Jetpack Compose instead of XML layouts.

**Rationale:**
- Modern, declarative UI framework
- Better state management
- Less boilerplate code
- Easier to maintain and test

### 2. Jetpack Glance for Widgets

**Decision:** Use Glance for app widget instead of RemoteViews.

**Rationale:**
- Compose-like API for widgets
- Easier to share logic with main app
- More maintainable than XML-based RemoteViews
- Material 3 theming support

### 3. DataStore for Persistence

**Decision:** Use DataStore Preferences instead of SharedPreferences.

**Rationale:**
- Type-safe
- Async by default (no ANR risk)
- Kotlin coroutines integration
- Better error handling

**Data Format:**
```
Timezones stored as: "America/New_York,Europe/London,Asia/Tokyo"
Separator: ","
```

### 4. AlarmManager for Widget Updates

**Decision:** Use AlarmManager instead of WorkManager for minute-by-minute updates.

**Rationale:**
- WorkManager minimum interval is 15 minutes
- AlarmManager allows exact minute-by-minute scheduling
- Uses `setExactAndAllowWhileIdle` for precision
- Falls back to inexact alarms if permission not granted

**Trade-offs:**
- Requires `SCHEDULE_EXACT_ALARM` permission on Android 12+
- Inexact fallback may have 1-2 minute delay

### 5. Reorderable Library (sh.calvin.reorderable)

**Decision:** Use external library for drag-to-reorder instead of custom implementation.

**Rationale:**
- Battle-tested gesture handling
- Proper integration with LazyColumn
- Haptic feedback support
- Clean API with `Modifier.draggableHandle()`

### 6. MVVM Architecture

**Structure:**
```
MainActivity.kt       - UI layer (Composables)
MainViewModel.kt      - Business logic, state management
TimezonePreferences.kt - Data layer (DataStore)
WorldClockWidget.kt   - Widget UI (Glance)
WorldClockWidgetReceiver.kt - Widget updates (AlarmManager)
```

**Data Flow:**
```
User Action → ViewModel → DataStore → StateFlow → UI Update
                      ↘ Widget.updateAll() → Widget Refresh
```

### 7. Single Activity Architecture

**Decision:** Use single Activity with Jetpack Navigation Compose.

**Rationale:**
- Simpler navigation
- Shared ViewModel across screens
- Better state preservation
- Modern Android best practice

---

## 📁 Project Structure

```
app/src/main/
├── java/com/worldclock/
│   ├── MainActivity.kt          # Main app UI
│   ├── MainViewModel.kt         # App state & logic
│   ├── data/
│   │   └── TimezonePreferences.kt  # DataStore persistence
│   ├── ui/
│   │   ├── TimezonePickerScreen.kt # Add timezone screen
│   │   └── theme/
│   │       └── Theme.kt         # Material 3 theming
│   └── widget/
│       ├── WorldClockWidget.kt  # Widget UI (Glance)
│       └── WorldClockWidgetReceiver.kt # Widget receiver
├── res/
│   ├── layout/                  # (minimal, Compose-based)
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── xml/
│       └── world_clock_widget_info.xml  # Widget metadata
└── AndroidManifest.xml
```

---

## 📝 Summary

The development workflow is designed for **autonomous AI development** with minimal human intervention:

1. **PLAN** — Know exactly what to change
2. **IMPLEMENT** — Make one small change
3. **BUILD** — Verify compilation
4. **QA** — Use computer-use MCP to visually verify
5. **REPEAT** — Move to next change only after verification

This iterative approach ensures quality, reduces debugging time, and allows the AI to work independently with confidence.
