# GitHub Actions Workflows

This project uses three GitHub Actions workflows for continuous integration and delivery.

---

## 1. Run Unit Tests (`run-tests.yml`)

**File:** `.github/workflows/run-tests.yml`

**Triggers:**
- `push` to `main` (skips `.md` changes)
- `pull_request` targeting `main` (skips `.md` changes)
- Manual trigger via `workflow_dispatch`

**Purpose:** Runs all Android unit tests on every PR and push to main.

**Steps:**

| Step | Action | Description |
|---|---|---|
| Checkout | `actions/checkout@v6` | Fetches the branch code |
| Setup Gradle | `gradle/actions/setup-gradle@v6` | Validates wrapper, sets up Gradle |
| Setup JDK 17 | `actions/setup-java@v5` | Temurin JDK 17, caches Gradle |
| Run tests | `./gradlew testDebugUnitTest` | Executes all debug unit tests |

---

## 2. Release APK (`gen-android-apk.yml`)

**File:** `.github/workflows/gen-android-apk.yml`

**Triggers:**
- `push` of a tag matching `v*` (e.g., `v1.2.3`)

**Purpose:** Builds a debug APK and creates a GitHub Release when a version tag is pushed.

**Steps:**

| Step | Action | Description |
|---|---|---|
| Checkout | `actions/checkout@v6` | Fetches the tagged commit |
| Setup Gradle | `gradle/actions/setup-gradle@v6` | Validates wrapper |
| Setup JDK 17 | `actions/setup-java@v5` | Temurin JDK 17, caches Gradle |
| Build APK + tests | `./gradlew test assembleDebug` | Runs tests and builds debug APK |
| Create Release | `gh release create` | Creates a GitHub Release for the tag |
| Upload APK | `gh release upload` | Attaches `app-debug.apk` to the release |

---

## 3. Maestro Android Tests (`maestro-tests.yml`)

**File:** `.github/workflows/maestro-tests.yml`

**Triggers:**
- Manual via `workflow_dispatch` (triggered from the Actions tab in GitHub)

**Purpose:** Starts the Go API server (HTTPS), builds the app with a local API URL, boots an Android emulator, installs the APK, and runs Maestro UI tests.

**Steps:**

| Step | Action | Description |
|---|---|---|
| Checkout | `actions/checkout@v6` | Fetches the PR branch |
| Setup Gradle | `gradle/actions/setup-gradle@v6` | Validates wrapper |
| Setup JDK 17 | `actions/setup-java@v5` | Temurin JDK 17, caches Gradle |
| Cache Maestro | `actions/cache@v5` | Caches Maestro CLI at `~/.maestro` |
| **Setup Go** | `actions/setup-go@v5` | Installs Go 1.22, caches modules |
| **Build + start API server** | `go build` + `./spacex-api-server &` | Builds and starts the Go server in background; waits for `/healthz` (up to 15s). Server uses certs from `certs/` and serves HTTPS on `:8443` |
| Build debug APK | `./gradlew assembleDebug -PAPI_BASE_URL=https://localhost:8443/` | Builds APK pointing at the Go server (via `adb reverse`) |
| Enable KVM | udev rules | Grants KVM access for the emulator |
| Start emulator + run tests | `reactivecircus/android-emulator-runner@v2` | Boots API 30 x86_64 emulator (Pixel 6 Pro), runs `setup_maestro.sh`, `install_app.sh`, `run_maestro.sh` |
| Comment PR | `actions/github-script@v9` | Posts test results on the PR (if applicable) |

### How the Go server integrates

1. The Go API server (`api-server/`) is built with `go build` and started in the background
2. It binds to `:8443` on the GitHub runner (HTTPS via pre-generated self-signed certs in `certs/`)
3. The Android APK is compiled with `-PAPI_BASE_URL=https://localhost:8443/`
4. `adb reverse tcp:8443 tcp:8443` forwards emulator connections to the host's Go server
5. The emulator boots and the app makes API calls to `localhost:8443`, which reaches the Go server on the runner
6. After tests complete, the server process is automatically cleaned up when the runner terminates
