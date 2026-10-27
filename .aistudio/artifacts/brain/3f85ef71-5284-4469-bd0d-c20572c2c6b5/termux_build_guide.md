# JARVIS Android App: Termux Build & Compilation Guide
## (টার্মাক্স দিয়ে অ্যান্ড্রয়েড অ্যাপ্লিকেশন বিল্ড করার সম্পূর্ণ নির্দেশিকা)

This guide provides the complete, tested procedure to clone, set up the Android build toolchain, and compile the **JARVIS** APK directly on your Android phone using **Termux**.

---

### 1. Prerequisites inside Termux
Ensure your Termux packages are updated and install required base packages (Git, OpenJDK 17, Wget, Unzip):

```bash
# Update Termux package repositories
pkg update && pkg upgrade -y

# Install OpenJDK 17, Git, and utilities
pkg install -y openjdk-17 git wget unzip aapt
```

Verify your Java version (Gradle 8+ / 9+ requires Java 17):
```bash
java -version
```

---

### 2. Grant Storage Permission
Allow Termux to access device storage:
```bash
termux-setup-storage
```

---

### 3. Setup Android Commandline Tools & SDK
Set up a lightweight Android SDK directory inside Termux:

```bash
# Create SDK directory structure
mkdir -p $HOME/android-sdk/cmdline-tools
cd $HOME/android-sdk/cmdline-tools

# Download Android SDK command line tools for Linux
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdline-tools.zip
unzip cmdline-tools.zip
mv cmdline-tools latest
rm cmdline-tools.zip

# Set Environment Variables in your ~/.bashrc
cat << 'EOF' >> ~/.bashrc
export ANDROID_HOME=$HOME/android-sdk
export ANDROID_SDK_ROOT=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
EOF

# Reload environment
source ~/.bashrc
```

Accept SDK licenses and install build tools & platform:
```bash
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

---

### 4. Clone or Copy the Project
Navigate to your project root or clone your repository:

```bash
# Example if exported or cloned from Git
git clone <YOUR_REPO_URL> jarvis-app
cd jarvis-app
```

---

### 5. Install Gradle (or use local Gradle)
Install Gradle in Termux:
```bash
pkg install -y gradle
```

Or download Gradle 8.11 / 9.0:
```bash
gradle -v
```

---

### 6. Build the APK

Run the debug build task:
```bash
# Build the Debug APK
gradle assembleDebug
```

> **Pro-Tip for Low-Memory Devices**:
> If your phone has 4GB RAM or less, pass the daemon & heap limits to prevent Termux OOM:
> ```bash
> gradle assembleDebug -Dorg.gradle.jvmargs="-Xmx1536m -XX:MaxMetaspaceSize=512m" --no-daemon
> ```

---

### 7. Locate & Install the Generated APK

Upon successful compilation, your APK is generated at:
```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

Copy the APK to your phone's `Download` folder so you can tap and install it:
```bash
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/JARVIS-debug.apk
```

Open your phone's File Manager, go to **Downloads**, and tap **`JARVIS-debug.apk`** to install JARVIS!
