# SpaceX API Server

A standalone Go HTTP server that replicates the archived [r-spacex/SpaceX-API](https://github.com/r-spacex/SpaceX-API), serving real and representative mock data so the Android app and Maestro tests work without the remote endpoint.

## Why

The upstream `r-spacex/SpaceX-API` was archived on Jun 6, 2026. The live endpoint `api.spacexdata.com` returns HTTP 521/525 (Cloudfare, server down). The backup site `backups.spacexdata.com` is also down.

Instead of relying on a dead remote service, this server runs locally (or in CI) and serves all original endpoints from static data snapshots.

## Quick Start

```sh
cd api-server

go run .                  # auto-detects certs in certs/ → HTTPS on :8443
go run . -port 9090       # custom port
```

The server auto-detects `certs/server.crt` and `certs/server.key`. If both exist it serves HTTPS; otherwise it falls back to HTTP.

```sh
# Verify it works
curl -sk https://localhost:8443/healthz
curl -sk https://localhost:8443/v5/launches | jq length
curl -sk https://localhost:8443/v4/company | jq '.name'
```

## Dependencies

Zero external dependencies. Only the Go 1.22 standard library:
- `net/http` — HTTP server and mux
- `encoding/json` — parse and encode JSON
- `embed` — embed data files into the binary
- `flag` — command-line flags
- `slices` — generic sort and search (Go 1.22+)
- `math` — ceiling for pagination math
- `os` — TLS cert file detection

## TLS

The server supports HTTPS via self-signed TLS certificates. Pre-generated certs live in `api-server/certs/` (valid for 100 years). The server auto-detects them at startup:

```
certs/
├── server.crt    # PEM-encoded X.509 certificate (SAN: localhost, 10.0.2.2, 127.0.0.1)
└── server.key    # RSA 2048-bit private key
```

The Android app embeds `server.crt` at `app/src/main/res/raw/server.crt` and trusts it via `network_security_config.xml` so HTTPS connections to the local server work without certificate errors on the emulator.

## Data Sources

| File | Records | Source |
|---|---|---|
| `launches_v5.json` | 205 | Wayback Machine |
| `launches_v4.json` | 205 | Copy of v5 (same data, different crew field format) |
| `capsules.json` | 25 | Wayback Machine |
| `crew.json` | 30 | Wayback Machine |
| `roadster.json` | 1 | Wayback Machine |
| `rockets.json` | 4 | Wayback Machine |
| `ships.json` | 29 | Wayback Machine |
| `company.json` | 1 | Generated |
| `cores.json` | 10 | Generated |
| `dragons.json` | 4 | Generated |
| `history.json` | 10 | Generated |
| `landpads.json` | 5 | Generated |
| `launchpads.json` | 4 | Generated |
| `payloads.json` | 10 | Generated |
| `starlink.json` | 5 | Generated |

## Architecture

```
main.go
  └── server/server.go           # http.ServeMux, CORS middleware, route registration
        └── server/handler.go    # //go:embed data/**, init(), all handler funcs
              └── server/data/   # 16 JSON files (embedded into binary)
```

**All data is embedded at compile time** via `//go:embed data/*.json`. The binary is self-contained — no external files needed at runtime.

## API Endpoints

### Launches (v5 + v4)

```
GET    /v{5,4}/launches               → list all (205)
GET    /v{5,4}/launches/{id}          → single by ID
GET    /v{5,4}/launches/latest        → most recent (max date_unix)
GET    /v{5,4}/launches/next          → first upcoming (date_unix ascending)
GET    /v{5,4}/launches/past          → past launches (upcoming=false)
GET    /v{5,4}/launches/upcoming      → upcoming launches (upcoming=true)
POST   /v{5,4}/launches/query         → paginated response
```

### Generic resources (v4)

Each follows the same pattern:

```
GET    /v4/{resource}            → list all
GET    /v4/{resource}/{id}       → single by id field
POST   /v4/{resource}/query      → paginated response
```

Resources: `capsules`, `cores`, `crew`, `dragons`, `history`, `landpads`, `launchpads`, `payloads`, `rockets`, `ships`, `starlink`

### Single-object resources

```
GET    /v4/company               → company info (single object)
GET    /v4/roadster              → roadster data (single object)
POST   /v4/roadster/query        → paginated (wraps in [roadster])
```

### Health

```
GET    /healthz                  → {"status": "ok"}
```

## Query Response Format

All `POST /{version}/{resource}/query` endpoints return:

```json
{
  "docs": [...],
  "totalDocs": 205,
  "limit": 10,
  "offset": 0,
  "page": 1,
  "totalPages": 21,
  "hasPrevPage": false,
  "hasNextPage": true,
  "prevPage": null,
  "nextPage": 2
}
```

Filters and sort are ignored; only `options.limit` and `options.offset` are respected (default limit=10, max=100).

## Tests

```sh
cd api-server && go test -v ./...
```

42 tests covering:
- `findByID` helper
- Query pagination (limits, offsets, edge cases)
- Generic resource handler (list, by ID, query, 404, 405)
- Launch handler (latest, next, past, upcoming, by ID, query, empty data)
- Single-object handlers (company, roadster, roadster query)
- Health endpoint
- CORS middleware
- Integration test hitting all real endpoints

## CORS

The server allows all origins (`Access-Control-Allow-Origin: *`) for local development. Preflight `OPTIONS` requests return 200 immediately.

## Error Handling

| Scenario | HTTP Status | Body |
|---|---|---|
| Unknown ID | 404 | `{"error": "Not found"}` |
| Wrong method | 405 | `{"error": "Method not allowed"}` |
| Empty data set for latest/next | 404 | `{"error": "No launches"}` |

## Design Decisions

1. **No reference expansion** — the original API supported `populate[]` to inline related objects. This server returns static JSON as-is. The Android app handles its own ID resolution.

2. **No query filtering** — POST /query parses the body for `options.limit`/`options.offset` but ignores filter/sort/select. The original API used MongoDB-like queries.

3. **v4/v5 launches share data** — both endpoints serve the same JSON. The `crew` field format differs (v5: objects, v4: strings) but since data is returned as raw blobs, the Android client interprets the format.

4. **Company has no /query** — matches the original API. Roadster does have /query (wraps the single object in a 1-element array).

5. **`//go:embed` requires data under server/** — the `embed` package resolves paths relative to the source file. Data files live in `server/data/` so `handler.go` can embed them with `data/*.json`.

## Integration with the Android App

The Android app reads the API base URL from `BuildConfig.API_BASE_URL` (defined in `core/network-retrofit/build.gradle.kts`):

```kotlin
defaultConfig {
    buildConfigField("String", "API_BASE_URL",
        "\"${project.findProperty("API_BASE_URL") ?: "https://localhost:8443/"}\"")
}
```

Override for CI or physical devices:

```sh
./gradlew assembleDebug -PAPI_BASE_URL=https://192.168.1.100:8443/
```

The app uses `https://localhost:8443/` and relies on `adb reverse tcp:8443 tcp:8443` to forward connections from the emulator to the host. Run this command once before building or launching:

```sh
adb reverse tcp:8443 tcp:8443
./gradlew installDebug -PAPI_BASE_URL=https://localhost:8443/
```

Alternatively, use the emulator alias `10.0.2.2` by overriding the URL:

```sh
./gradlew installDebug -PAPI_BASE_URL=https://10.0.2.2:8443/
```

The emulator alias `10.0.2.2` maps to the host machine's `localhost`, so the app running in the emulator reaches the Go server on the host.
