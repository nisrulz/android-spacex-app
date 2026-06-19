#!/usr/bin/env bash

set -e # Exit immediately if a command exits with a non-zero status.

# Set up port forwarding for API server
adb reverse tcp:8443 tcp:8443

# Wait for emulator to boot
adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done; input keyevent 82'

echo "✅ Emulator active now"

# Install prebuilt APK
adb install -r app/build/outputs/apk/debug/app-debug.apk
