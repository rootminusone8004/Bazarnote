# Build Information

## Project Information
  - **Project Name:** Bazarnote
  - **Package Name:** com.rootminusone8004.bazarnote

## Build Environment
  - **IntelliJ IDEA (Community Edition):** 2024.3.4
  - **Gradle Version:** 8.7
  - **Java Version:** 11

## SDK and tools
  - **compileSdkVersion:** 34
  - **buildToolsVersion:** 34
  - **minSdkVersion:** 24
  - **targetSdkVersion:** 34

## Gradle
  - **AGP Version** = "8.5.2"

## Dependencies
  - `androidx.cardview:cardview:1.0.0`
  - `androidx.lifecycle:lifecycle-livedata:2.8.3`
  - `androidx.lifecycle:lifecycle-viewmodel:2.8.3`
  - `androidx.room:room-runtime:2.6.1`
  - `com.opencsv:opencsv:5.5.2`
  - `com.google.code.gson:gson:2.10.1`

## Build Status
  - **Success:** Yes

---

## Build Instructions

### Common steps

1. **Clone the repository**
    - Make sure [git](https://git-scm.com) is installed.

    ```bash
    sudo pacman -S git
    ```

    - Open a terminal and clone the project repository:

    ```bash
    $ git clone -b devel "https://github.com/rootminusone8004/Bazarnote"
    $ cd Bazarnote
    ```

### IntelliJ IDEA

1. **Install prerequisites**
    _Notice_: The commands given apply for *Arch Linux and its derivatives*.

    - Ensure you have the following packages installed:
      - [IDEA](https://www.jetbrains.com/idea): In Arch Linux, the community version is available in the *extra* repository.

    ```bash
    sudo pacman -S intellij-idea-community-edition
    ```

    _Note_: Make sure your IDE is set properly for android development.

      - Java Development Kit (JDK): Here, [openjdk](https://openjdk.org) is used.

    ```bash
    sudo pacman -S jdk11-openjdk
    ```

      - [Gradle](https://gradle.org/install): It can be installed via IntelliJ IDEA.
    
2. **Open the project in Android Studio**
    - Launch Android Studio.
    - Select the option of opening an existing project.
    - Navigate to the cloned project repository and select it.

3. **Sync project with gradle files**
    - After the project opens, sync it with gradle files.
    - Wait for the sync process to complete.

4. **Configure build variants (if any)**
    - Open the build variants option.
    - Select the desired build variant.

5. **Build the project**
    - To build the APK, click on *Build* in the menu bar.
    - Select Build *Bundle(s) / APK(s)* > *Build APK(s)*.

6. **Run the app**
    - To run the app on the emulator or connected device, click the green play button in the toolbar.
    - Ensure you have an emulator configured or a device connected via USB with developer mode enabled.

### Terminal

1. **Install prerequisites**

    _Notice_: The commands given apply for *Arch Linux and its derivatives*.

    - Ensure you have the following packages installed:
      - Java Development Kit (JDK): Here, [openjdk](https://openjdk.org) is used. Make sure that, it is of version **17**.

    ```bash
    sudo pacman -S jdk17-openjdk
    ```

      - [Gradle](https://gradle.org/install):

    ```bash
    sudo pacman -S gradle
    ```

      - adb:

    ```bash
      sudo pacman -S android-tools
    ```

2. **Open the project in terminal**

    ```bash
    cd Bazarnote
    ```

3. **Create necessary files for building**

    ```bash
    gradle wrapper
    ```

4. **Build the project**
    
    To build the APK, run the following command:

    ```bash
    ./gradlew build
    ```

    You will get the apk file in _./app/build/outputs/apk/release_ directory.

5. **Install the app**

    ```bash
    adb install app-release-unsigned.apk
    ```

## Download

You can get the app from here:
  1. [GitHub](https://github.com/rootminusone8004/Bazarnote/releases)
  2. [F-Droid](https://apt.izzysoft.de/fdroid/index/apk/com.rootminusone8004.bazarnote)

_Notice_: [F-Droid](https://f-droid.org) is recommended for downloading the app. But for that **you must add [IzzyOnDroid](https://apt.izzysoft.de) repo**. You can add it from [here](https://apt.izzysoft.de/fdroid/index.php).
