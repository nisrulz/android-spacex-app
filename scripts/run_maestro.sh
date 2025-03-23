#!/usr/bin/env bash

set -e # Exit immediately if a command exits with a non-zero status.

# Wait for emulator to boot
adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'

echo "✅ Emulator active now"

# Install APK
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk

# Install Maestro CLI
curl -Ls "https://get.maestro.mobile.dev" | bash
export PATH="$HOME/.maestro/bin:$PATH"
export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED=true
export MAESTRO_CLI_NO_ANALYTICS=1

# Run Maestro Tests
export APP_ID="com.nisrulz.example.spacexapi"
maestro test -e APP_ID=$APP_ID .maestro/
