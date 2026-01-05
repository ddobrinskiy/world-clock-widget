# QA Checklist - World Clock Android App

This checklist covers all features that should be tested before each release.

---

## 📱 Main App Features

### Adding Timezones
- [ ] Tap the **+** button to open timezone picker
- [ ] Search for a city (e.g., "Tokyo")
- [ ] Select a timezone from the list
- [ ] Verify new timezone appears in the main list
- [ ] Verify correct time is displayed for the timezone
- [ ] Verify UTC offset is displayed correctly

### Removing Timezones
- [ ] Swipe left on a timezone card
- [ ] Verify red delete background appears
- [ ] Complete the swipe to delete
- [ ] Verify timezone is removed from the list
- [ ] Verify deletion persists after app restart

### Reordering Timezones
- [ ] Drag the handle (≡) on a timezone card
- [ ] Move it to a new position
- [ ] Verify haptic feedback on drag start
- [ ] Verify the item moves smoothly
- [ ] Release and verify new order is saved
- [ ] Restart app and verify order persists

### Home Timezone
- [ ] Swipe right on a card → card becomes home (accented color + 🏠 emoji)
- [ ] Swipe right on home card again → card reverts to normal
- [ ] Swipe right on different card while another is home → new card becomes home (only one home at a time)
- [ ] Verify home status persists after app restart
- [ ] Verify 🏠 emoji appears to the left of city name

### Time Display
- [ ] Verify times update automatically every minute
- [ ] Verify times are accurate for each timezone
- [ ] Verify 24-hour format is used (HH:mm)

---

## 🔲 Widget Features

### Widget Placement
- [ ] Long press on home screen
- [ ] Select "Widgets"
- [ ] Find "World Clock" widget
- [ ] Drag to home screen
- [ ] Verify widget displays timezones

### Widget Time Updates
- [ ] Note the current time shown in widget
- [ ] Wait 60 seconds
- [ ] Verify widget time has updated
- [ ] Time should update within ~1-2 minutes

### Widget Size Adaptation
- [ ] Resize widget to smallest size (3x2)
- [ ] Verify it shows 1-2 timezones
- [ ] Resize widget to medium size
- [ ] Verify it shows more timezones
- [ ] Resize widget to large size
- [ ] Verify it shows even more timezones

### Widget-App Sync
- [ ] Add a new timezone in the app
- [ ] Verify it appears in the widget
- [ ] Remove a timezone in the app
- [ ] Verify it disappears from the widget
- [ ] Reorder timezones in the app
- [ ] Verify order updates in widget

---

## 💾 Data Persistence

### App Restart
- [ ] Add/remove/reorder timezones
- [ ] Force close the app
- [ ] Reopen the app
- [ ] Verify all changes persisted

### Device Restart
- [ ] Add/remove/reorder timezones
- [ ] Restart the device
- [ ] Open the app
- [ ] Verify all changes persisted
- [ ] Verify widget still shows correct data

---

## 🎨 UI/UX

### Visual Elements
- [ ] Cards have proper elevation/shadow
- [ ] Drag handle icon is visible on each card
- [ ] Delete background shows delete icon
- [ ] FAB (+) button is visible and tappable
- [ ] Top app bar displays "World Clock"

### Animations
- [ ] Card elevation increases when dragging
- [ ] Smooth reorder animation when moving items
- [ ] Swipe-to-delete animation is smooth

### Haptic Feedback
- [ ] Haptic on drag start
- [ ] Haptic during drag movement
- [ ] Haptic on drag end

---

## 🐛 Edge Cases

### Empty State
- [ ] Delete all timezones
- [ ] Verify app handles empty list gracefully
- [ ] Add a timezone to non-empty state

### Many Timezones
- [ ] Add 10+ timezones
- [ ] Verify scrolling works smoothly
- [ ] Verify reordering works with many items

### Invalid Timezone
- [ ] App should not crash with any timezone
- [ ] All IANA timezone IDs should work

---

## 📋 Quick Smoke Test

For rapid testing before commits:

1. [ ] Open app - timezones display correctly
2. [ ] Add a timezone - appears in list
3. [ ] Reorder - drag and drop works
4. [ ] Delete - swipe left removes item
5. [ ] Widget - shows correct times
6. [ ] Widget - updates within 2 minutes

---

## 🔧 Test Commands

### Install and Launch
```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
./gradlew installDebug
$ANDROID_HOME/platform-tools/adb shell am start -n com.worldclock/.MainActivity
```

### Take Screenshot
```bash
$ANDROID_HOME/platform-tools/adb shell screencap -p /sdcard/screenshot.png
$ANDROID_HOME/platform-tools/adb pull /sdcard/screenshot.png ~/Downloads/qa_screenshot.png
```

### View Logs
```bash
$ANDROID_HOME/platform-tools/adb logcat | grep -i worldclock
```

### Clear App Data (Fresh Start)
```bash
$ANDROID_HOME/platform-tools/adb shell pm clear com.worldclock
```

---

## ✅ Sign-off

| Version | Date | Tester | Result |
|---------|------|--------|--------|
| 1.0.0   |      |        | ⬜     |

**Legend:**
- ✅ All tests passed
- ⚠️ Minor issues found
- ❌ Critical issues found

