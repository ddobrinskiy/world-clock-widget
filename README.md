# World Clock Widget - Android

A minimalistic Android app for tracking time across multiple timezones with home screen widgets.

## Features

- 📍 **Add Multiple Timezones** - Add and manage timezones from around the world
- 🕐 **24-Hour Format** - Clean display with 24-hour time format only
- 📱 **Home Screen Widget** - Resizable widget showing all your selected timezones
- 🎨 **Material Design 3** - Modern UI with dynamic theming (Material You)
- 🌓 **Dark Mode Support** - Automatic light/dark theme switching
- 🔋 **Battery Efficient** - Smart updates using WorkManager

## Screenshots

### Main App
- List view of all selected timezones
- Each timezone shows: city name, UTC offset, current time (24h), and date
- Swipe to delete timezones
- Search and add new timezones

### Widget
- Resizable widget for your home screen
- Displays multiple timezones with their current times
- Tap to open the main app
- Auto-updates every 15 minutes

## Technical Details

### Built With
- **Kotlin** - Modern Android development language
- **Jetpack Compose** - Declarative UI framework
- **Glance** - Jetpack Compose for widgets
- **Material Design 3** - Latest Material Design with dynamic theming
- **DataStore** - Modern data storage for preferences
- **WorkManager** - Efficient background widget updates
- **MVVM Architecture** - Clean separation of concerns

### Requirements
- Android 8.0 (API 26) or higher
- No special permissions required
- No internet connection needed

## Setup & Installation

### Prerequisites
- Android Studio (latest version recommended)
- JDK 17 or higher
- Android SDK with API 34

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd vc-android-world-clock
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory
   - Wait for Gradle sync to complete

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" (▶️) or press `Shift + F10`
   - The app will install and launch automatically

### Build APK
```bash
./gradlew assembleRelease
```
The APK will be generated in `app/build/outputs/apk/release/`

## Usage

### Adding Timezones
1. Open the app
2. Tap the **+** floating action button
3. Search for a city or browse by region
4. Tap a timezone to add it to your list

### Removing Timezones
- Swipe left on any timezone card to delete it

### Adding Widget
1. Long-press on your home screen
2. Tap "Widgets"
3. Find "World Clock Widget"
4. Drag it to your home screen
5. Resize as needed - the widget adapts to show more or fewer timezones

## Project Structure

```
app/src/main/java/com/worldclock/
├── data/
│   ├── model/          # Data models (TimezoneData)
│   ├── repository/     # Repository layer
│   └── datastore/      # DataStore implementation
├── ui/
│   ├── screens/        # Main and Picker screens
│   ├── components/     # Reusable UI components
│   └── theme/          # Material Design 3 theme
├── widget/             # Widget implementation (Glance)
├── viewmodel/          # ViewModel layer
└── MainActivity.kt     # Main entry point
```

## Widget Updates

The widget updates using WorkManager with the following strategy:
- **Automatic updates**: Every 15 minutes (minimum period for WorkManager)
- **Manual updates**: When timezones are added/removed
- **Efficient**: Uses minimal battery with smart scheduling

## Customization

### Changing Colors
Edit `app/src/main/java/com/worldclock/ui/theme/Color.kt` to customize the color scheme.

### Widget Layout
Modify `app/src/main/java/com/worldclock/widget/WorldClockWidget.kt` to adjust widget appearance.

### Time Format
Currently hardcoded to 24-hour format. To change, modify the `DateTimeFormatter` in `TimezoneData.kt`.

## Known Limitations

- Widget updates minimum every 15 minutes (Android WorkManager limitation)
- No time zone converter feature (by design - keep it minimal)
- No custom nicknames for timezones (by design - keep it minimal)

## Contributing

This is a personal project, but suggestions and bug reports are welcome!

## License

This project is open source and available under the MIT License.

## Acknowledgments

- Material Design 3 guidelines by Google
- Jetpack Glance for widget support
- Java Time API for timezone handling

---

**Note**: This app requires Android 8.0 or higher and works completely offline. No permissions, no ads, no tracking.
