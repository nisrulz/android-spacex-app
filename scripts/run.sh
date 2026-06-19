#!/bin/bash
set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "Setting up adb reverse port forwarding..."
adb reverse tcp:8443 tcp:8443 > /dev/null 2>&1 || true

echo "Stopping any existing Go API server..."
pkill -f "go run.*api-server" > /dev/null 2>&1 || true
pkill -f "spacex-api-server" > /dev/null 2>&1 || true

echo "Starting Go API server in background (HTTPS)..."
(cd "$ROOT/api-server" && go run .) > /tmp/api-server.log 2>&1 &
SERVER_PID=$!

sleep 3

echo "Building and installing debug APK..."
cd "$ROOT" && ./gradlew installDebug -q -PAPI_BASE_URL=https://localhost:8443/

echo "Launching app on connected device..."
SERIAL=$(adb devices | awk 'NR>1 && $1!="" {print $1; exit}')
adb -s "$SERIAL" shell am start -n com.nisrulz.example.spacexapi/.presentation.features.MainActivity > /dev/null 2>&1

echo "App launched. Server PID=$SERVER_PID (logs in /tmp/api-server.log)"
wait $SERVER_PID
