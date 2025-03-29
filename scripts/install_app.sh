#!/usr/bin/env bash

set -e # Exit immediately if a command exits with a non-zero status.

# Wait for emulator to boot
adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'

echo "✅ Emulator active now"

# Install APK
./gradlew installDebug
