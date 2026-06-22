.PHONY: help install start-app start-server stop-server run run-retrofit-room run-retrofit-sqldelight run-ktor-room run-ktor-sqldelight test lint cleanup

help:
	@echo "Targets:"
	@echo "  install                adb reverse + build and install debug APK"
	@echo "  start-app              Launch app on connected device"
	@echo "  start-server           Start Go API server (foreground)"
	@echo "  stop-server            Stop Go API server"
	@echo "  run                    Full flow: stop -> start-server (bg) -> install -> start-app"
	@echo "  run-retrofit-room     Run with Retrofit + Room (default)"
	@echo "  run-retrofit-sqldelight Run with Retrofit + SQLDelight"
	@echo "  run-ktor-room         Run with Ktor + Room"
	@echo "  run-ktor-sqldelight   Run with Ktor + SQLDelight"
	@echo "  test                   Run Android unit tests + Go server tests"
	@echo "  lint                   Run Android lint checks"
	@echo "  cleanup                Delete all build artifacts"

install:
	@adb reverse tcp:8443 tcp:8443 > /dev/null 2>&1 || true
	@echo "Building and installing APK..."
	@./gradlew installDebug -q -PAPI_BASE_URL=https://localhost:8443/

start-app:
	@adb -s $$(adb devices | awk 'NR>1 && $$1!="" {print $$1; exit}') shell am start -n com.nisrulz.example.spacexapi/.presentation.features.MainActivity > /dev/null 2>&1

start-server:
	@echo "Starting Go API server on :8443..."
	@cd api-server && go run . 2>/dev/null

stop-server:
	@-pkill -f "go run.*api-server" > /dev/null 2>&1; pkill -f "spacex-api-server" > /dev/null 2>&1; true

run:
	@scripts/run.sh

run-retrofit-room:
	@scripts/run.sh -PNETWORK_IMPL=retrofit -PSTORAGE_IMPL=room

run-retrofit-sqldelight:
	@scripts/run.sh -PNETWORK_IMPL=retrofit -PSTORAGE_IMPL=sqldelight

run-ktor-room:
	@scripts/run.sh -PNETWORK_IMPL=ktor -PSTORAGE_IMPL=room

run-ktor-sqldelight:
	@scripts/run.sh -PNETWORK_IMPL=ktor -PSTORAGE_IMPL=sqldelight

test:
	@echo "Running Android unit tests..."
	@./gradlew testDebugUnitTest -q
	@echo "Running Go server tests..."
	@cd api-server && go test -count=1 ./...
	@if adb devices | grep -q "device$$"; then \
		echo "Running Maestro UI tests..."; \
		scripts/run_maestro.sh; \
	else \
		echo "Skipping Maestro tests (no device connected)"; \
	fi

lint:
	@echo "Running Android lint..."
	@./gradlew lint -q

cleanup:
	@echo "Cleaning Android build artifacts..."
	@./gradlew clean -q
	@echo "Cleaning Go server binary..."
	@cd api-server && go clean
