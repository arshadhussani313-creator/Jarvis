# JARVIS AI: Stage 2 Foundation

Compile-ready Android foundation: Gradle config, manifest, Compose theme,
animated reactor, waveform, HUD status tiles, real device status (battery,
network), real app launching (YouTube, WhatsApp, Settings, browser), typed
local commands, and bottom navigation. Gemini, voice, accessibility,
notifications, WhatsApp automation, coding workspace, and settings persistence
come in later stages.

## What works in this stage
- Battery level and charging state (public BatteryManager API).
- Network status (ConnectivityManager, ACCESS_NETWORK_STATE).
- Open YouTube (app if installed, else youtube.com), open WhatsApp (if installed), open phone Settings.
- YouTube search from typed command: `search youtube for <text>`.
- Typed commands: `open youtube`, `open whatsapp`, `open settings`, `battery`.
- Recent operations list reporting real outcomes only.

## What does NOT work yet (honest)
- Gemini / AI replies: not connected. Free-form text is answered with "not connected".
- Voice input: disabled until Stage 3.
- Flashlight, timers, volume, camera, maps, accessibility taps: Stage 4.
- WhatsApp notifications and reply drafts: Stage 5.
- Settings screen contents: Stage 6.

## Requirements
- Android Studio Ladybug (2024.2.x) or newer, JDK 17.
- Android SDK Platform 34 and Build-Tools 34.0.0.
- Gradle 8.9 (the wrapper is not included; Android Studio or `gradle wrapper --gradle-version 8.9` creates it).
- Android phone with API 29+ (Android 10+).

## Build with Android Studio (Path A)
1. Unzip, then File > Open > select the `JarvisAI` folder.
2. Let Gradle sync. Android Studio writes `local.properties` with your SDK path.
3. Build > Build APK(s). Output: `app/build/outputs/apk/debug/app-debug.apk`.
4. On the phone: enable "Install unknown apps" for your file manager, then open the APK.

## Build on a PC without Android Studio (command line)
```
cd JarvisAI
gradle wrapper --gradle-version 8.9      # creates gradlew
./gradlew assembleDebug
```
Set `ANDROID_HOME` to your SDK path, or put `sdk.dir=/path/to/Android/Sdk` in `local.properties`.

## Build from a phone (Termux)
On-device Android builds with Gradle and AAPT2 are unreliable in Termux, so this
project is not expected to build there. Two supported options:
- Push the project to GitHub. The included `.github/workflows/android-build.yml`
  builds the debug APK on GitHub's servers. Download it from the run's Artifacts.
- Use Termux only to copy/transfer the APK and run `adb install` from a PC.

## Test checklist for Stage 2
1. App opens without crashing; dark theme, cyan reactor animates.
2. Clock ticks each second; date shows.
3. Battery and network tiles show real values (turn Wi-Fi off, tile shows Offline).
4. Tap "Open YouTube": YouTube opens, or browser opens youtube.com.
5. Tap "Open WhatsApp" without WhatsApp installed: shows "not installed" (no crash).
6. Type `battery` and press Send: shows the real percentage in Recent Operations.
7. Type `hello`: shows "not connected" message (no fake AI reply).
8. Enable Android "Remove animations": reactor and waveform stop moving.
9. Bottom tabs switch screens; placeholder tabs state their stage.

## Important notes
- `compileSdk`/`targetSdk` are 34 to match AGP 8.5.2. Google Play requires newer targets for publishing; raise both once you upgrade AGP.
- Versions are pinned in `gradle/libs.versions.toml`. Verify them in Android Studio when syncing; upgrade only together.
- Nothing here collects personal data or sends anything to a server.
