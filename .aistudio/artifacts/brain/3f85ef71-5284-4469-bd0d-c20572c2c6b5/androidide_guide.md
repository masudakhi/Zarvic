# JARVIS Android App: AndroidIDE Build Guide
## (AndroidIDE দিয়ে অ্যাপ তৈরি ও বিল্ড করার নিয়ম)

This guide explains how to extract the project ZIP, open it in the **AndroidIDE** mobile app on your Android device, and build the APK.

---

### 1. Download AndroidIDE on your Phone
If you haven't installed AndroidIDE yet:
- Download the official APK from GitHub: [AndroidIDE Releases](https://github.com/AndroidIDE-Android/AndroidIDE/releases) or F-Droid.
- Open AndroidIDE and complete the initial setup (it will download the required JDK and Android SDK tools automatically).

---

### 2. Extract the Project ZIP File
The project is packaged in **`jarvis-androidide-project.zip`**:

1. Move `jarvis-androidide-project.zip` to your phone's internal storage or `Projects` folder (e.g. `/sdcard/Projects/`).
2. Extract / Unzip the archive into a folder named `JARVIS`.
3. Verify that the folder contains:
   - `app/`
   - `gradle/`
   - `gradlew` and `gradlew.bat`
   - `build.gradle.kts`
   - `settings.gradle.kts`

---

### 3. Open in AndroidIDE
1. Launch **AndroidIDE**.
2. Tap **Open Project**.
3. Navigate to the extracted `JARVIS` folder and tap **Select**.
4. AndroidIDE will scan `settings.gradle.kts` and sync dependencies with Gradle.

---

### 4. Build the APK
1. In the top toolbar, tap the **Hammer** (Build) icon or open the built-in terminal in AndroidIDE.
2. In the terminal, run:
   ```bash
   ./gradlew assembleDebug
   ```
3. Once the build completes with `BUILD SUCCESSFUL`, the APK is created at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```
4. Tap **Run** (Play button) or tap the generated APK in the file tree to install and launch JARVIS on your phone!
