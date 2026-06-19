# SpaceX API Server — Local Go Replacement

The upstream `r-spacex/SpaceX-API` was archived on Jun 6, 2026. The live endpoint `api.spacexdata.com` returns 521/525 (Cloudflare, server down). The backup site `backups.spacexdata.com` is also down.

This document describes the plan to create a standalone Go API server in `api-server/` that serves all original SpaceX-API endpoints from static data snapshots.

---

## Status

- [x] Research complete
- [x] Plan documented
- [x] `api-server/` Go project scaffolded
- [x] Data files sourced (Wayback Machine + generated mock)
- [x] Android app wired to use local server
- [x] GitHub Actions updated to start server before emulator

---

## 1. Project Structure

```
<android-spacex-app-root>
├── api-server/                        ← Standalone Go project (NOT a Gradle module)
│   ├── go.mod                         # module github.com/nisrulz/spacex-api-server, go 1.22
│   ├── main.go                        # flag.Parse("-port"), calls server.ListenAndServe()
│   ├── server/
│   ├── server/
│   │   ├── server.go                  # NewServer() *http.ServeMux, embedded ListenAndServe, CORS middleware
│   │   ├── handler.go                 # //go:embed data/*.json, init() parse, all Handle* funcs
│   │   └── data/                      # Embedded by go:embed (must be under server/ dir)
│   │       ├── launches_v5.json       # Real data — 205 records (Wayback Machine)
│   │       ├── launches_v4.json       # Same data as v5; served on /v4/launches/* with crew as string[]
│   │       ├── capsules.json          # Real data — 25 records (Wayback Machine)
│   │       ├── crew.json              # Real data — 30 records (Wayback Machine)
│   │       ├── roadster.json          # Real data — 1 JSON object (Wayback Machine)
│   │       ├── rockets.json           # Real data — 4 records (Wayback Machine)
│   │       ├── ships.json             # Real data — 29 records (Wayback Machine)
│   │       ├── company.json           # Generated mock — single JSON object
│   │       ├── cores.json             # Generated mock — array
│   │       ├── dragons.json           # Generated mock — array
│   │       ├── history.json           # Generated mock — array
│   │       ├── landpads.json          # Generated mock — array
│   │       ├── launchpads.json        # Generated mock — array
│   │       ├── payloads.json          # Generated mock — array
│   │       └── starlink.json          # Generated mock — array
├── app/
├── core/
├── .github/workflows/
│   └── maestro-tests.yml             ← Updated to build & start Go server
└── scripts/
```

**IMPORTANT:** `api-server/` is a standalone Go module with no Gradle/Android dependencies. It must NOT be referenced in `settings.gradle.kts` or any Gradle config.

**Module path:** `github.com/nisrulz/spacex-api-server` (used in `go.mod` and imports)

---

## 2. Dependencies

The Go server uses only the **standard library** — no external dependencies. Specifically:

- `net/http` — HTTP server and mux
- `encoding/json` — parse and encode JSON
- `embed` — embed data files
- `flag` — command-line flags
- `log` — logging
- `strings` — path manipulation
- `slices` — finding items in slices (Go 1.22+, available in Go 1.22)
- `strconv` — parsing query parameters
- `math` — ceiling for pagination math

`go.mod` requires only `go 1.22` (no `require` blocks).

---

## 3. Data Sources

### Wayback Machine (real data)

| File | Records | Source |
|------|---------|--------|
| `data/launches_v5.json` | 205 | `web.archive.org/web/20251006050535id_/https://api.spacexdata.com/v5/launches` |
| `data/capsules.json` | 25 | `web.archive.org/web/20260206123459id_/https://api.spacexdata.com/v4/capsules` |
| `data/crew.json` | 30 | `web.archive.org/web/20251128000000id_/https://api.spacexdata.com/v4/crew` |
| `data/roadster.json` | 1 | `web.archive.org/web/20251006050535id_/https://api.spacexdata.com/v4/roadster` |
| `data/rockets.json` | 4 | `web.archive.org/web/20251128034223id_/https://api.spacexdata.com/v4/rockets` |
| `data/ships.json` | 29 | `web.archive.org/web/20251128000000id_/https://api.spacexdata.com/v4/ships` |

All Wayback URLs use the `id_` modifier to get raw JSON content. The response is gzip-encoded; append `?` to the browser URL bar to disable encoding before saving. The fetched data must be saved exactly as-is (the JSON array or object).

### Generated mock data (schema-compliant)

For the 8 resources with no Wayback captures, generate representative mock data that matches the documented schemas:

#### `company.json` — 1 object (not an array)
```json
{
  "name": "SpaceX",
  "founded": 2002,
  "employees": 13000,
  "vehicles": 5,
  "launch_sites": 4,
  "test_sites": 2,
  "ceo": "Elon Musk",
  "cto": "Elon Musk",
  "coo": "Gwynne Shotwell",
  "cto_propulsion": "Tom Mueller",
  "valuation": 210000000000,
  "headquarters": {
    "address": "Rocket Road",
    "city": "Hawthorne",
    "state": "California"
  },
  "links": {
    "elon_twitter": "https://x.com/elonmusk",
    "flickr": "https://www.flickr.com/spacex/",
    "twitter": "https://x.com/SpaceX",
    "website": "https://www.spacex.com/"
  },
  "summary": "SpaceX designs, manufactures and launches advanced rockets and spacecraft. The company was founded in 2002 to revolutionize space technology, with the ultimate goal of enabling people to live on other planets."
}
```

#### `cores.json` — 10 records (array)
Each core follows this structure (use realistic values):
```json
{
  "id": "5e9e28a7f35918100003b265",
  "serial": "B1019",
  "block": 3,
  "status": "active",
  "reuse_count": 3,
  "rtls_landings": 3,
  "asds_landings": 0,
  "water_landings": 0,
  "last_update": "Last known February 9, 2021",
  "launches": ["5eb87ce3ffd86e000604b33a"],
  "wikipedia": "https://en.wikipedia.org/wiki/Falcon_9_B1019",
  "failures": null,
  "legs_landed_count": 3,
  "legs_landing_pad": null,
  "asds_attempts": 0,
  "asds_landings": 0,
  "block_landings": null,
  "rtls_attempts": 3,
  "rtls_landings": 3
}
```
Generate 10 cores: B1019, B1021, B1023, B1025, B1029, B1031, B1035, B1046, B1049, B1050 with varying statuses (active, inactive, lost, expended), reuse_count from 0–10, and matching launch ID references. The IDs should be realistic-looking hex strings (24 chars, starting with `5e9e...`).

#### `dragons.json` — 4 records (array)
Each dragon:
```json
{
  "id": "5e9d058759b1ff74a7ad5f34",
  "name": "Dragon 1",
  "type": "capsule",
  "active": false,
  "crew_capacity": 0,
  "sidewall_angle_deg": 15,
  "orbit_duration_yr": 2,
  "dry_mass_kg": 4200,
  "dry_mass_lb": 9300,
  "first_flight": "2010-12-08",
  "heat_shield": {
    "material": "PICA-X",
    "size_meters": 3.6,
    "temp_degrees": 1927,
    "dev_partner": "NASA"
  },
  "thrusters": [...],
  "launch_payload_mass": {"kg": 6000, "lb": 13228},
  "launch_payload_vol": {"cubic_meters": 10, "cubic_feet": 353},
  "return_payload_mass": {"kg": 3000, "lb": 6614},
  "return_payload_vol": {"cubic_meters": 7, "cubic_feet": 247},
  "cargo": {...},
  "trunk": {...},
  "height_w_trunk": {"meters": 7.2, "feet": 23.6},
  "diameter": {"meters": 3.7, "feet": 12.1},
  "wikipedia": "https://en.wikipedia.org/wiki/SpaceX_Dragon",
  "description": "Dragon 1 is a reusable cargo spacecraft developed by SpaceX."
}
```
Generate 4 dragons: Dragon 1, Dragon 2 Crew (crew_capacity: 7), Dragon 2 Cargo, Dragon 2 Developed Cargo. Copy realistic thruster/cargo/trunk specs from Wikipedia.

#### `history.json` — 10 records (array)
```json
{
  "id": "5f6fb2cfdcfdf403dd54cabc",
  "title": "Falcon 1 first successful launch",
  "event_date_utc": "2008-09-28T23:15:00.000Z",
  "event_date_unix": 1222643700,
  "details": "Falcon 1 became the first privately developed liquid fuel rocket to reach orbit.",
  "links": {
    "article": "https://en.wikipedia.org/wiki/Falcon_1_flight_4"
  }
}
```
Generate 10 events spanning 2006–2023: first Falcon 1 launch (failure), first successful orbit, first Dragon to ISS, first booster landing, first Crew Dragon, first Starship flight, etc.

#### `landpads.json` — 5 records (array)
```json
{
  "id": "5e9e3039383ecf267a34e7b6",
  "name": "LZ-1",
  "full_name": "Landing Zone 1",
  "status": "active",
  "type": "landing_zone",
  "locality": "Cape Canaveral",
  "region": "Florida",
  "latitude": 28.485794,
  "longitude": -80.544648,
  "landing_attempts": 20,
  "landing_successes": 20,
  "wikipedia": "https://en.wikipedia.org/wiki/Landing_Zone_1",
  "details": "SpaceX's first landing zone, located at Cape Canaveral Space Force Station."
}
```
5 pads: LZ-1, LZ-2, OCISLY, JRTI, LZ-4. OCISLY and JRTI are ASDS (autonomous spaceport drone ship), type should be `"asds"` not `"landing_zone"`. Include realistic coordinates and landing counts.

#### `launchpads.json` — 4 records (array)
```json
{
  "id": "5e9e4502f5090947855666f0",
  "name": "KSLC 39A",
  "full_name": "Kennedy Space Center Historic Launch Complex 39A",
  "status": "active",
  "locality": "Cape Canaveral",
  "region": "Florida",
  "latitude": 28.608056,
  "longitude": -80.603889,
  "launch_attempts": 100,
  "launch_successes": 98,
  "wikipedia": "https://en.wikipedia.org/wiki/Kennedy_Space_Center_Launch_Complex_39A",
  "details": "NASA's historic launch complex, leased by SpaceX in 2014.",
  "launches": ["5eb87ce3ffd86e000604b33a"]
}
```
4 pads: KSC LC-39A, CCSFS SLC-40, VSFB SLC-4E, Boca Chica. Each with realistic coordinates, launch counts, and a few launch ID refs.

#### `payloads.json` — 10 records (array)
```json
{
  "id": "5eb0e4b5b6c3bb0006c1bb21",
  "name": "DemoSat",
  "type": "Satellite",
  "reused": false,
  "launch": "5eb87cd9ffd86e000604b337",
  "customers": ["SpaceX"],
  "nationalities": ["United States"],
  "manufacturers": ["SpaceX"],
  "mass_kg": 165,
  "mass_lbs": 363.76,
  "orbit": "LEO",
  "reference_system": "geocentric",
  "regime": "low-earth",
  "longitude": null,
  "semi_major_axis_km": 6692.29,
  "eccentricity": 0.0013281,
  "periapsis_km": 315,
  "apoapsis_km": 319,
  "inclination_deg": 51.6,
  "period_min": 90.89,
  "lifespan_years": 1,
  "epoch": "2021-03-03T12:34:56.000Z",
  "mean_motion": 15.85,
  "raan": 152.6
}
```
Generate 10 payloads: a few small demo satellites, a few Starlink batches, a few ISS cargo missions, a few Crew Dragon, a few large comsats. Match ID format and link to any launch that references them.

#### `starlink.json` — 5 records (array)
```json
{
  "id": "5eb0e4b5b6c3bb0006c1bb22",
  "spaceTrack": {
    "CCSDS_OMM_VERS": "2.0",
    "COMMENT": "GENERATED VIA SPACE-TRACK.ORG",
    "CREATION_DATE": "2021-02-28T12:34:56",
    "ORIGINATOR": "18 SPCS",
    "OBJECT_NAME": "STARLINK-1007",
    "OBJECT_ID": "2021-012A",
    "CENTER_NAME": "EARTH",
    "REF_FRAME": "EME2000",
    "TIME_SYSTEM": "UTC",
    "MEAN_MOTION": 15.06403782,
    "ECCENTRICITY": 0.0001406,
    "INCLINATION": 53.0543,
    "RA_OF_ASC_NODE": 167.6259,
    "ARG_OF_PERICENTER": 91.5802,
    "MEAN_ANOMALY": 268.5545,
    "EPOCH": "2021-03-03T12:34:56.000Z",
    "REV_AT_EPOCH": 14,
    "BSTAR": 0.0001,
    "MEAN_MOTION_DOT": 0.000013,
    "MEAN_MOTION_DDOT": 0
  },
  "launch": "5eb87ce3ffd86e000604b33a",
  "version": "v1.0"
}
```
Generate 5 Starlink satellites with realistic spaceTrack orbital parameters (53-degree inclination for v1.0, 70-degree or 97.6-degree for other shells). Link launch to one of the launches in the dataset.

### ID Format Convention

All SpaceX-API IDs use 24-character hex strings starting with `5e9` or `5eb`. Generated mock IDs must follow this format:
- Launch IDs: `5eb87ce3ffd86e000604b33a` (reuse existing launch IDs where possible)
- Capsule IDs: `5e9e2c01f35918300003b265`
- Core IDs: `5e9e28a7f35918100003b265`
- Crew IDs: `5ebf1a6e23a9a60006c03d87`
- Dragon IDs: `5e9d058759b1ff74a7ad5f34`
- History IDs: `5f6fb2cfdcfdf403dd54cabc`
- Landpad IDs: `5e9e3039383ecf267a34e7b6`
- Launchpad IDs: `5e9e4502f5090947855666f0`
- Payload IDs: `5eb0e4b5b6c3bb0006c1bb21`
- Rocket IDs: `5e9d0d95eda69955f709d1eb`
- Ship IDs: `5ea6ed2d080df4000697c901`
- Starlink IDs: `5eb0e4b5b6c3bb0006c1bb22`

Generated IDs should keep the `5e` prefix and use a unique hex suffix. Do NOT duplicate IDs across resource types.

### Reference between resources

Where launches reference capsules, cores, crew, payloads, rockets, or ships by ID, use IDs that actually exist in the respective data files. This is critical for the API's correctness.

HOWEVER — the Go server returns data as static JSON blobs; it does NOT expand references. The Android app does its own ID resolution. So references just need to not be empty strings. But for quality, they should be semi-consistent.

---

## 4. Complete API Route Map

All endpoints from the original SpaceX-API are served exactly as documented. The Go server uses Go 1.22+ `http.ServeMux` which supports `{id}` path parameters natively.

### Launches (v5)

```
GET    /v5/launches           → list all (205)
GET    /v5/launches/{id}      → single by id
GET    /v5/launches/latest    → most recent (first in list when sorted by date_usec descending)
GET    /v5/launches/next      → first where upcoming=true (sorted by date_unix ascending)
GET    /v5/launches/past      → filter: upcoming=false (sorted by date_unix descending)
GET    /v5/launches/upcoming  → filter: upcoming=true (sorted by date_unix ascending)
POST   /v5/launches/query     → paginated response
```

**Sublime paths must match in exact order.** `/latest`, `/next`, `/past`, `/upcoming` are sub-routes, not IDs. The handler must check these BEFORE the generic `{id}` fallback.

### Launches (v4)

Same routes as v5, but served from the same `launches_v4.json` file (or just transformed at runtime from v5 data — same records, but `crew` field format differs: v4 uses `[]string` of IDs, v5 uses `[]object`):

```
GET    /v4/launches           → same records as v5
GET    /v4/launches/{id}
GET    /v4/launches/latest
GET    /v4/launches/next
GET    /v4/launches/past
GET    /v4/launches/upcoming
POST   /v4/launches/query
```

Simplest approach: make `launches_v4.json` a symlink or copy of `launches_v5.json`. The data is served as raw JSON blobs — the Android client interprets the `crew` field format, not the server.

### All Other Resources (v4)

These all follow the same 3-endpoint pattern:

```
GET    /v4/{resource}         → list all as JSON array
GET    /v4/{resource}/{id}    → single item by id field
POST   /v4/{resource}/query   → paginated response
```

Resources using this pattern:
- capsules, cores, crew, dragons, history, landpads, launchpads, payloads, rockets, ships, starlink

### Special Resources (single object, not array)

```
GET    /v4/company           → single JSON object (not an array)
GET    /v4/roadster          → single JSON object (not an array)
POST   /v4/roadster/query    → paginated response with 1 doc (treat as array of 1)
```

`/v4/company` does NOT have a `/:id` route or `/query` route (per original API).
`/v4/roadster` DOES have a `/query` route (it pages a single-element array).

### Health

```
GET    /healthz              → 200 OK, body: {"status":"ok"}
```

### Query Endpoint Response Format (all resources)

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

**Request body parsing (for query):**

```go
type QueryBody struct {
    Select  *map[string]any `json:"select"`   // ignored — always return all fields
    Sort    *string         `json:"sort"`      // ignored — return as-is
    Populate *[]any         `json:"populate"`  // ignored — no reference expansion
    Options QueryOptions    `json:"options"`
}

type QueryOptions struct {
    Limit  *int `json:"limit"`   // default 10, max 100
    Offset *int `json:"offset"`  // default 0
}
```

**Pagination math:**

```go
totalDocs := len(data)
limit := 10
if opts.Limit != nil { limit = *opts.Limit }
offset := 0
if opts.Offset != nil { offset = *opts.Offset }

// Clamp
if limit < 1 { limit = 10 }
if limit > 100 { limit = 100 }
if offset < 0 { offset = 0 }
if offset >= totalDocs {
    docs = []any{}
} else {
    end := offset + limit
    if end > totalDocs { end = totalDocs }
    docs = data[offset:end]
}

totalPages := (totalDocs + limit - 1) / limit
page := (offset / limit) + 1
hasNextPage := page < totalPages
hasPrevPage := page > 1
var prevPage any
if hasPrevPage { prevPage = page - 1 }
var nextPage any
if hasNextPage { nextPage = page + 1 }
```

---

## 5. Go Server Architecture (Exact Implementation)

### `api-server/go.mod`

```
module github.com/nisrulz/spacex-api-server

go 1.22
```

No `require` block — standard library only.

### `api-server/main.go`

```go
package main

import (
	"flag"
	"log"
	"github.com/nisrulz/spacex-api-server/server"
)

func main() {
	port := flag.String("port", "8080", "port to listen on")
	flag.Parse()
	log.Printf("SpaceX API server starting on :%s", *port)
	log.Fatal(server.ListenAndServe(":" + *port))
}
```

### `api-server/server/server.go`

```go
package server

import (
	"log"
	"net/http"
	"strings"
)

func corsMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
		if r.Method == http.MethodOptions {
			w.WriteHeader(http.StatusOK)
			return
		}
		next.ServeHTTP(w, r)
	})
}

// ListenAndServe creates the mux, registers all routes, wraps with CORS, and listens.
func ListenAndServe(addr string) error {
	mux := http.NewServeMux()

	// Launches v5 — all subroutes (/{id}, /latest, /next, /past, /upcoming, /query)
	mux.HandleFunc("/v5/launches/", handleV5Launches)
	mux.HandleFunc("/v5/launches", handleV5Launches)

	// Launches v4
	mux.HandleFunc("/v4/launches/", handleV4Launches)
	mux.HandleFunc("/v4/launches", handleV4Launches)

	// Resource groups — each follows same {base}/ and {base} pattern
	registerResourceRoutes(mux, "/v4/capsules", handleCapsules)
	registerResourceRoutes(mux, "/v4/cores", handleCores)
	registerResourceRoutes(mux, "/v4/crew", handleCrew)
	registerResourceRoutes(mux, "/v4/dragons", handleDragons)
	registerResourceRoutes(mux, "/v4/history", handleHistory)
	registerResourceRoutes(mux, "/v4/landpads", handleLandpads)
	registerResourceRoutes(mux, "/v4/launchpads", handleLaunchpads)
	registerResourceRoutes(mux, "/v4/payloads", handlePayloads)
	registerResourceRoutes(mux, "/v4/rockets", handleRockets)
	registerResourceRoutes(mux, "/v4/ships", handleShips)
	registerResourceRoutes(mux, "/v4/starlink", handleStarlink)

	// Single-object resources
	mux.HandleFunc("/v4/company", handleCompany)
	mux.HandleFunc("/v4/roadster", handleRoadster)
	mux.HandleFunc("/v4/roadster/query", handleRoadsterQuery)

	// Health
	mux.HandleFunc("/healthz", handleHealth)

	log.Printf("Routes registered:")
	log.Printf("  GET /healthz")
	log.Printf("  GET /v5/launches, /v5/launches/{id}, /latest, /next, /past, /upcoming")
	log.Printf("  POST /v5/launches/query")
	log.Printf("  GET /v4/launches, /v4/launches/{id}, /latest, /next, /past, /upcoming")
	log.Printf("  POST /v4/launches/query")
	for _, name := range resourceNames {
		log.Printf("  GET /v4/%s, GET /v4/%s/{id}, POST /v4/%s/query", name, name, name)
	}
	log.Printf("  GET /v4/company")
	log.Printf("  GET /v4/roadster, POST /v4/roadster/query")

	return http.ListenAndServe(addr, corsMiddleware(mux))
}

func registerResourceRoutes(mux *http.ServeMux, prefix string, handler http.HandlerFunc) {
	mux.HandleFunc(prefix+"/", handler)
	mux.HandleFunc(prefix, handler)
}
```

### `api-server/server/handler.go` — Complete

```go
package server

import (
	"embed"
	"encoding/json"
	"log"
	"math"
	"net/http"
	"slices"
	"strconv"
	"strings"
)

//go:embed data/*.json
var dataFS embed.FS

// ─── Data types ───────────────────────────────────────────────────────────

type queryBody struct {
	Options struct {
		Limit  *int `json:"limit"`
		Offset *int `json:"offset"`
	} `json:"options"`
}

type queryResponse struct {
	Docs        []any  `json:"docs"`
	TotalDocs   int    `json:"totalDocs"`
	Limit       int    `json:"limit"`
	Offset      int    `json:"offset"`
	Page        int    `json:"page"`
	TotalPages  int    `json:"totalPages"`
	HasPrevPage bool   `json:"hasPrevPage"`
	HasNextPage bool   `json:"hasNextPage"`
	PrevPage    *int   `json:"prevPage"`
	NextPage    *int   `json:"nextPage"`
}

// ─── Parsed data (populated by init()) ────────────────────────────────────

var (
	capsules   []any
	cores      []any
	crew       []any
	dragons    []any
	history    []any
	landpads   []any
	launchpads []any
	payloads   []any
	rockets    []any
	ships      []any
	starlink   []any

	launchesV5 []any
	launchesV4 []any

	company  any    // single object
	roadster any    // single object
)

var resourceNames = []string{
	"capsules", "cores", "crew", "dragons", "history",
	"landpads", "launchpads", "payloads", "rockets", "ships", "starlink",
}

func init() {
	// Load launches (v5)
	mustParse("data/launches_v5.json", &launchesV5)
	mustParse("data/launches_v4.json", &launchesV4)

	// Load resource arrays
	mustParse("data/capsules.json", &capsules)
	mustParse("data/cores.json", &cores)
	mustParse("data/crew.json", &crew)
	mustParse("data/dragons.json", &dragons)
	mustParse("data/history.json", &history)
	mustParse("data/landpads.json", &landpads)
	mustParse("data/launchpads.json", &launchpads)
	mustParse("data/payloads.json", &payloads)
	mustParse("data/rockets.json", &rockets)
	mustParse("data/ships.json", &ships)
	mustParse("data/starlink.json", &starlink)

	// Load single-object resources
	mustParse("data/company.json", &company)
	mustParse("data/roadster.json", &roadster)

	log.Printf("Loaded %d launches (v5), %d launches (v4)", len(launchesV5), len(launchesV4))
	log.Printf("Loaded resources: capsules=%d, cores=%d, crew=%d, dragons=%d, history=%d, landpads=%d, launchpads=%d, payloads=%d, rockets=%d, ships=%d, starlink=%d",
		len(capsules), len(cores), len(crew), len(dragons), len(history),
		len(landpads), len(launchpads), len(payloads), len(rockets), len(ships), len(starlink))
}

func mustParse(path string, target any) {
	data, err := dataFS.ReadFile(path)
	if err != nil {
		log.Fatalf("Failed to read embedded %s: %v", path, err)
	}
	if err := json.Unmarshal(data, target); err != nil {
		log.Fatalf("Failed to parse %s: %v", path, err)
	}
}

// ─── Helpers ──────────────────────────────────────────────────────────────

func writeJSON(w http.ResponseWriter, status int, data any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(data)
}

func writeError(w http.ResponseWriter, status int, msg string) {
	writeJSON(w, status, map[string]string{"error": msg})
}

// findAllByID searches a slice of map objects for one whose "id" key matches.
// Returns nil if not found.
func findByID(data []any, id string) any {
	idx := slices.IndexFunc(data, func(item any) bool {
		m, ok := item.(map[string]any)
		if !ok {
			return false
		}
		v, ok := m["id"]
		return ok && v == id
	})
	if idx == -1 {
		return nil
	}
	return data[idx]
}

// handleQuery processes a POST /query request with pagination.
func handleQuery(w http.ResponseWriter, r *http.Request, data []any) {
	if r.Method != http.MethodPost {
		writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
		return
	}

	var body queryBody
	if err := json.NewDecoder(r.Body).Decode(&body); err != nil {
		// Accept empty body — proceed with defaults
	}

	limit := 10
	if body.Options.Limit != nil && *body.Options.Limit > 0 {
		limit = *body.Options.Limit
	}
	if limit > 100 {
		limit = 100
	}

	offset := 0
	if body.Options.Offset != nil && *body.Options.Offset > 0 {
		offset = *body.Options.Offset
	}

	totalDocs := len(data)

	totalPages := int(math.Ceil(float64(totalDocs) / float64(limit)))
	if totalPages < 1 {
		totalPages = 1
	}
	page := (offset / limit) + 1
	if page < 1 {
		page = 1
	}

	// Slice the data
	var docs []any
	if offset >= totalDocs {
		docs = []any{}
	} else {
		end := offset + limit
		if end > totalDocs {
			end = totalDocs
		}
		docs = data[offset:end]
	}

	hasNextPage := page < totalPages
	hasPrevPage := page > 1
	var prevPage *int
	if hasPrevPage {
		p := page - 1
		prevPage = &p
	}
	var nextPage *int
	if hasNextPage {
		n := page + 1
		nextPage = &n
	}

	writeJSON(w, http.StatusOK, queryResponse{
		Docs:        docs,
		TotalDocs:   totalDocs,
		Limit:       limit,
		Offset:      offset,
		Page:        page,
		TotalPages:  totalPages,
		HasPrevPage: hasPrevPage,
		HasNextPage: hasNextPage,
		PrevPage:    prevPage,
		NextPage:    nextPage,
	})
}

// genericResourceHandler returns an http.HandlerFunc for a resource array.
// This handles:
//   GET  /v4/{resource}         → list all
//   GET  /v4/{resource}/{id}    → find by id (404 if not found)
//   POST /v4/{resource}/query   → paginated query
func genericResourceHandler(data []any) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		// Extract the sub-path after the base prefix.
		// r.URL.Path is already the full path; we extract the last segment.
		// Go 1.22 ServeMux puts {id} into r.PathValue("id") when using patterns like "GET /v4/{resource}/{id}"
		// BUT since we registered with trailing slash for catch-all, Go 1.22 won't parse {id}.
		// Instead we manually extract.
		parts := strings.Split(strings.Trim(r.URL.Path, "/"), "/")
		// parts will be ["v4", "capsules"] or ["v4", "capsules", "someid"] or ["v4", "capsules", "query"]
		// If the URL had a trailing slash, there may be an empty string at the end.
		// We always consume parts[0]=="v4" and parts[1]==resource name.

		switch r.Method {
		case http.MethodGet:
			if len(parts) <= 2 || parts[2] == "" {
				// GET /v4/{resource} — list all
				writeJSON(w, http.StatusOK, data)
				return
			}
			id := parts[2]
			item := findByID(data, id)
			if item == nil {
				writeError(w, http.StatusNotFound, "Not found")
				return
			}
			writeJSON(w, http.StatusOK, item)

		case http.MethodPost:
			if len(parts) > 2 && parts[2] == "query" {
				handleQuery(w, r, data)
				return
			}
			writeError(w, http.StatusNotFound, "Not found")

		default:
			writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
		}
	}
}

// ─── Launch handler (special: sub-routes /latest, /next, /past, /upcoming) ─

// launchHandler handles launch endpoints with special sub-routes.
// data is the full launch array; toSingle is an optional transform that converts
// a launch element if v4 needs different format (nil if no transform needed).
func launchHandler(data []any) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		// Parse path: /v{5,4}/launches[/...]
		parts := strings.Split(strings.Trim(r.URL.Path, "/"), "/")
		// parts = ["v5", "launches"] or ["v5", "launches", "latest"] etc.

		if r.Method == http.MethodGet {
			if len(parts) <= 2 || parts[2] == "" {
				// GET /v5/launches — list all
				writeJSON(w, http.StatusOK, data)
				return
			}

			sub := parts[2]
			switch sub {
			case "latest":
				// Most recent launch: sort by date_unix descending, take first
				if len(data) == 0 {
					writeError(w, http.StatusNotFound, "No launches")
					return
				}
				latest := data[0] // assume data is already sorted with latest first; if not, find max
				for _, item := range data {
					m1 := item.(map[string]any)
					m0 := latest.(map[string]any)
					t1, _ := m1["date_unix"].(float64)
					t0, _ := m0["date_unix"].(float64)
					if t1 > t0 {
						latest = item
					}
				}
				writeJSON(w, http.StatusOK, latest)

			case "next":
				// First upcoming launch (upcoming=true), sorted by date_unix ascending
				var upcoming []any
				for _, item := range data {
					m := item.(map[string]any)
					if up, ok := m["upcoming"].(bool); ok && up {
						upcoming = append(upcoming, item)
					}
				}
				if len(upcoming) == 0 {
					writeError(w, http.StatusNotFound, "No upcoming launches")
					return
				}
				slices.SortFunc(upcoming, func(a, b any) int {
					ma := a.(map[string]any)
					mb := b.(map[string]any)
					ta, _ := ma["date_unix"].(float64)
					tb, _ := mb["date_unix"].(float64)
					if ta < tb { return -1 }
					if ta > tb { return 1 }
					return 0
				})
				writeJSON(w, http.StatusOK, upcoming[0])

			case "past":
				// Filter: upcoming=false, sort by date_unix descending
				var past []any
				for _, item := range data {
					m := item.(map[string]any)
					if up, ok := m["upcoming"].(bool); ok && !up {
						past = append(past, item)
					}
				}
				slices.SortFunc(past, func(a, b any) int {
					ma := a.(map[string]any)
					mb := b.(map[string]any)
					ta, _ := ma["date_unix"].(float64)
					tb, _ := mb["date_unix"].(float64)
					if ta > tb { return -1 }
					if ta < tb { return 1 }
					return 0
				})
				writeJSON(w, http.StatusOK, past)

			case "upcoming":
				// Filter: upcoming=true, sort by date_unix ascending
				var upcoming []any
				for _, item := range data {
					m := item.(map[string]any)
					if up, ok := m["upcoming"].(bool); ok && up {
						upcoming = append(upcoming, item)
					}
				}
				slices.SortFunc(upcoming, func(a, b any) int {
					ma := a.(map[string]any)
					mb := b.(map[string]any)
					ta, _ := ma["date_unix"].(float64)
					tb, _ := mb["date_unix"].(float64)
					if ta < tb { return -1 }
					if ta > tb { return 1 }
					return 0
				})
				writeJSON(w, http.StatusOK, upcoming)

			default:
				// Try as {id}
				item := findByID(data, sub)
				if item == nil {
					writeError(w, http.StatusNotFound, "Not found")
					return
				}
				writeJSON(w, http.StatusOK, item)
			}
		} else if r.Method == http.MethodPost {
			if len(parts) > 2 && parts[2] == "query" {
				handleQuery(w, r, data)
				return
			}
			writeError(w, http.StatusNotFound, "Not found")
		} else {
			writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
		}
	}
}

// ─── Individual handler variables (set at init time) ─────────────────────

var (
	handleV5Launches http.HandlerFunc
	handleV4Launches http.HandlerFunc
	handleCapsules   http.HandlerFunc
	handleCores      http.HandlerFunc
	handleCrew       http.HandlerFunc
	handleDragons    http.HandlerFunc
	handleHistory    http.HandlerFunc
	handleLandpads   http.HandlerFunc
	handleLaunchpads http.HandlerFunc
	handlePayloads   http.HandlerFunc
	handleRockets    http.HandlerFunc
	handleShips      http.HandlerFunc
	handleStarlink   http.HandlerFunc
)

func init() {
	// Create handlers after data is loaded
	handleV5Launches = launchHandler(launchesV5)
	handleV4Launches = launchHandler(launchesV4)
	handleCapsules = genericResourceHandler(capsules)
	handleCores = genericResourceHandler(cores)
	handleCrew = genericResourceHandler(crew)
	handleDragons = genericResourceHandler(dragons)
	handleHistory = genericResourceHandler(history)
	handleLandpads = genericResourceHandler(landpads)
	handleLaunchpads = genericResourceHandler(launchpads)
	handlePayloads = genericResourceHandler(payloads)
	handleRockets = genericResourceHandler(rockets)
	handleShips = genericResourceHandler(ships)
	handleStarlink = genericResourceHandler(starlink)
}

// ─── Company and Roadster handlers ────────────────────────────────────────

func handleCompany(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
		return
	}
	writeJSON(w, http.StatusOK, company)
}

func handleRoadster(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
		return
	}
	writeJSON(w, http.StatusOK, roadster)
}

func handleRoadsterQuery(w http.ResponseWriter, r *http.Request) {
	// roadster is a single object; treat as array of 1 for query
	if r.Method != http.MethodPost {
		writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
		return
	}
	roadsterArr := []any{roadster}
	handleQuery(w, r, roadsterArr)
}

// ─── Health ───────────────────────────────────────────────────────────────

func handleHealth(w http.ResponseWriter, r *http.Request) {
	writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
}
```

### CRITICAL: Late-stage handler assignment

The handler variables (`handleV5Launches`, `handleCapsules`, etc.) must be initialized in a **second `init()` function** that runs AFTER the data-loading `init()`. Go guarantees that init() functions in a single file run in declaration order, so:

1. First `init()` — parses embedded JSON into data variables
2. Second `init()` — creates handlers that reference the now-populated data variables

Alternatively, handlers could use a lazy-load pattern (check if data is nil), but two init() functions are cleaner.

**BEST APPROACH:** Use a single `init()` that:
1. Parses data
2. Assigns handler variables

--- BEGIN single init() approach ---

```go
func init() {
	// 1. Load data
	mustParse("data/launches_v5.json", &launchesV5)
	mustParse("data/launches_v4.json", &launchesV4)
	mustParse("data/capsules.json", &capsules)
	// ... all other data files ...

	// 2. Create handlers
	handleV5Launches = launchHandler(launchesV5)
	handleV4Launches = launchHandler(launchesV4)
	handleCapsules = genericResourceHandler(capsules)
	// ... all other handlers ...
}
```

--- END ---

### `data/launches_v4.json` handling

`launches_v4.json` should be a **copy** of `launches_v5.json` (not a symlink, since go:embed does not follow symlinks). The v4 data is structurally identical to v5 in the JSON file. The Android app interprets the `crew` field format (v4 expects []string, v5 expects []object), but since we're returning raw JSON, the data is exactly the same from the server's perspective.

---

## 6. Complete Route Registration Table

| Handler Var | Base Path | Routes | Method Behavior |
|---|---|---|---|
| `handleV5Launches` | `/v5/launches` | `GET /v5/launches`, `GET /v5/launches/{id}`, `GET /v5/launches/latest`, `GET /v5/launches/next`, `GET /v5/launches/past`, `GET /v5/launches/upcoming`, `POST /v5/launches/query` | `GET /v5/launches` → all; `GET /v5/launches/{id}` → single; `GET /v5/launches/latest` → latest; `GET /v5/launches/next` → next; sub-routes checked before id fallback |
| `handleV4Launches` | `/v4/launches` | Same as v5 | Same as v5 |
| `handleCapsules` | `/v4/capsules` | `GET /v4/capsules`, `GET /v4/capsules/{id}`, `POST /v4/capsules/query` | `GET` with path only → list; `GET` with sub-path → `findByID`; `POST` with `/query` → paginate; else 404 |
| `handleCores` | `/v4/cores` | Same pattern | Same |
| `handleCrew` | `/v4/crew` | Same pattern | Same |
| `handleDragons` | `/v4/dragons` | Same pattern | Same |
| `handleHistory` | `/v4/history` | Same pattern | Same |
| `handleLandpads` | `/v4/landpads` | Same pattern | Same |
| `handleLaunchpads` | `/v4/launchpads` | Same pattern | Same |
| `handlePayloads` | `/v4/payloads` | Same pattern | Same |
| `handleRockets` | `/v4/rockets` | Same pattern | Same |
| `handleShips` | `/v4/ships` | Same pattern | Same |
| `handleStarlink` | `/v4/starlink` | Same pattern | Same |
| `handleCompany` | `/v4/company` | `GET /v4/company` | Only GET; returns single object |
| `handleRoadster` | `/v4/roadster` | `GET /v4/roadster` | Only GET; returns single object |
| `handleRoadsterQuery` | `/v4/roadster/query` | `POST /v4/roadster/query` | Only POST; wraps roadster as [roadster] and paginates |
| `handleHealth` | `/healthz` | `GET /healthz` | Returns `{"status":"ok"}` |

---

## 7. Android App Changes

### Files to modify

#### `core/network-retrofit/build.gradle.kts`

Enable `buildConfig` and add the `API_BASE_URL` build config field. The exact file is at:
`core/network-retrofit/build.gradle.kts`

Current content (approximate — read the actual file before editing):
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
// ...
android {
    namespace = "com.nisrulz.spacex.core.network.retrofit"
    // ...
    buildFeatures {
        buildConfig = true   // ADD THIS
    }
    defaultConfig {
        // ...
        buildConfigField("String", "API_BASE_URL",
            project.findProperty("API_BASE_URL") as? String
                ?: "http://10.0.2.2:8080/")   // ADD THIS
    }
}
```

#### `core/network-retrofit/src/main/kotlin/.../di/NetworkModule.kt`

Exact path: (find via glob `core/network-retrofit/**/di/NetworkModule.kt`)

Current code uses a constant like `SpaceXLaunchesApi.BASE_URL` or a hardcoded string. Replace the base URL argument in `Retrofit.Builder().baseUrl(...)` with:
```kotlin
import com.nisrulz.spacex.core.network.retrofit.BuildConfig
// ... in provideRetrofit() ...
Retrofit.Builder()
    .baseUrl(BuildConfig.API_BASE_URL)
    // ... rest unchanged
```

#### `core/network-retrofit/src/main/kotlin/.../SpaceXLaunchesApi.kt`

Exact path: (find via glob `core/network-retrofit/**/SpaceXLaunchesApi.kt`)

Remove the `BASE_URL` constant from the companion object (or wherever it's defined). The interface declaration, `@GET`, `@POST` annotations, and parameter definitions must remain untouched.

#### `core/network-retrofit/src/test/resources/response_items_list.json`

Replace the file content with the full 205-record launch dataset (the same content as `data/launches_v5.json`).

### Tests

Find test file: `core/network-retrofit/src/test/java/.../SpaceXLaunchesApiTest.kt`

There are 3 tests that use `MockWebServer` with `MockWebServerHelper.generateRetrofit<SpaceXLaunchesApi>(mockWebServer)`. These create their own Retrofit instance independent of the DI module, so they require zero changes.

The 4th test (`test BASE_URL is correct`) was removed because `BASE_URL` constant was deleted from `SpaceXLaunchesApi`.

**Test results:** `./gradlew :core:network-retrofit:test` passes (3 tests, 0 failures).

---

## 8. GitHub Actions Changes

### `.github/workflows/maestro-tests.yml`

Read the existing file first at `.github/workflows/maestro-tests.yml`.

Add after the "Cache Maestro CLI" step (or after the Gradle setup step, before "Build debug APK"):

```yaml
      - name: Set up Go
        uses: actions/setup-go@v5
        with:
          go-version: '1.22'
          cache: true
          cache-dependency-path: api-server/go.sum

      - name: Build and start API server
        run: |
          cd api-server
          go build -o spacex-api-server .
          ./spacex-api-server &
          echo "Waiting for API server to be ready..."
          for i in $(seq 1 15); do
            if curl -sf http://localhost:8080/healthz > /dev/null 2>&1; then
              echo "API server ready"
              break
            fi
            echo "  attempt $i/15..."
            sleep 1
          done
```

Important integration note: The debug APK build command uses `-PAPI_BASE_URL=http://10.0.2.2:8080/` explicitly (emulator host alias `10.0.2.2`). The Go server binds to `localhost:8080` on the runner. Emulator reaches the host machine at `10.0.2.2`.

**Full build pipeline flow:**

1. Checkout code
2. Setup Java 17
3. Setup Gradle
4. Setup Go 1.22
5. Build Go API server (`go build`)
6. Start Go API server in background (`./spacex-api-server &`)
7. Health-check loop (curl /healthz, up to 15 attempts, 1s apart)
8. Build Android debug APK (`./gradlew assembleDebug -PAPI_BASE_URL=http://10.0.2.2:8080/`)
9. Enable KVM
10. Start Android emulator (wait for boot)
11. Install APK on emulator (`./gradlew installDebug` or `scripts/install_app.sh`)
12. Run Maestro tests (`scripts/run_maestro.sh`)
13. Post results comment on PR

---

## 9. Implementation Order

| Step | Action | Files | Expected Output |
|------|--------|-------|-----------------|
| 1 | Create Go module | `api-server/go.mod` | `go build ./...` succeeds |
| 2 | Create main.go | `api-server/main.go` | `go run .` shows "Starting on :8080" |
| 3 | Create server.go + handler.go | `api-server/server/server.go`, `handler.go` | Server compiles |
| 4 | Add data/launches_v5.json | `api-server/data/launches_v5.json` (205 records) | `curl localhost:8080/v5/launches` returns array |
| 5 | Add data/launches_v4.json (copy of v5) | `api-server/data/launches_v4.json` | `curl localhost:8080/v4/launches` returns array |
| 6 | Add Wayback data files (capsules, crew, roadster, rockets, ships) | 5 JSON files | Each endpoint returns expected data |
| 7 | Generate and add mock data files (company, cores, dragons, history, landpads, launchpads, payloads, starlink) | 8 JSON files | Each endpoint returns expected data |
| 8 | Smoke test all endpoints | — | All 50+ routes return correct responses |
| 9 | Edit build.gradle.kts | `core/network-retrofit/build.gradle.kts` | BuildConfig available in module |
| 10 | Edit NetworkModule.kt | `core/network-retrofit/.../di/NetworkModule.kt` | Uses BuildConfig.API_BASE_URL |
| 11 | Remove BASE_URL from SpaceXLaunchesApi.kt | `core/network-retrofit/.../SpaceXLaunchesApi.kt` | BASE_URL constant removed |
| 12 | Replace test mock data | `core/network-retrofit/.../test/resources/response_items_list.json` | 205 records |
| 13 | Run unit tests | — | `./gradlew test` passes |
| 14 | Update GitHub Actions | `.github/workflows/maestro-tests.yml` | CI adds Go setup+build+start steps |

---

## 10. Verification Checklist

### Server build and run

```sh
# Build
cd api-server && go build -o spacex-api-server .
echo "Build: $?"  # must be 0

# Run server in background
./spacex-api-server &
SERVER_PID=$!
sleep 1

# Health check
curl -sf http://localhost:8080/healthz && echo "HEALTH: OK"

# Resource endpoints (spot-check a few)
curl -sf http://localhost:8080/v5/launches | jq length   # 205
curl -sf http://localhost:8080/v5/launches/latest | jq '.id'
curl -sf http://localhost:8080/v5/launches/next | jq '.id'
curl -sf http://localhost:8080/v5/launches/past | jq length
curl -sf http://localhost:8080/v5/launches/upcoming | jq length
curl -sf http://localhost:8080/v4/company | jq '.name'    # "SpaceX"
curl -sf http://localhost:8080/v4/rockets | jq length     # 4
curl -sf http://localhost:8080/v4/capsules | jq length    # 25
curl -sf http://localhost:8080/v4/crew | jq length        # 30
curl -sf http://localhost:8080/v4/ships | jq length       # 29

# Single by ID
FIRST_ID=$(curl -sf http://localhost:8080/v5/launches | jq -r '.[0].id')
curl -sf "http://localhost:8080/v5/launches/$FIRST_ID" | jq '.id'
echo "Single lookup: $?"  # must be 0, id must match

# Query
curl -sf -X POST http://localhost:8080/v5/launches/query \
  -H 'Content-Type: application/json' \
  -d '{"options":{"limit":5,"offset":0}}' | jq '.docs | length'  # 5

# 404 handling
curl -sf http://localhost:8080/v5/launches/nonexistent
echo "Expected 404: $?"  # curl exit code 22 (HTTP 4xx/5xx)

# CORS headers
curl -sI http://localhost:8080/v5/launches | grep -i access-control-allow-origin
echo "CORS header present: $?"  # must be 0

# Kill server
kill $SERVER_PID
```

### Android build

```sh
# Build with default URL (emulator)
./gradlew :core:network-retrofit:assembleDebug
echo "Module build: $?"  # must be 0

# Run tests
./gradlew :core:network-retrofit:test
echo "Tests: $?"  # must be 0

# Full APK build (optional)
./gradlew assembleDebug
echo "APK build: $?"  # must be 0
```

### CI check (GitHub Actions)

After updating `.github/workflows/maestro-tests.yml`, verify the workflow has:
- `actions/setup-go@v5` with `go-version: '1.22'`
- `go build` command in the `api-server/` directory
- Server background process with health check
- `-PAPI_BASE_URL=http://10.0.2.2:8080/` on the Gradle build step

---

## 11. Known Constraints / Edge Cases

1. **No reference expansion.** The original SpaceX-API expanded populated fields (e.g., `populate=rocket` would include the full rocket object). This server does NOT implement that — it returns static JSON exactly as stored.

2. **No filtering in queries.** `POST /query` accepts any JSON body but ignores `filter`, `sort`, and `select` fields. Only `options.limit` and `options.offset` are respected. The original API supported MongoDB-like query filters; this server does not implement them.

3. **Crew field format (v4 vs v5).** In v5, `crew` is an array of objects with `{crew, role}`. In v4, it's an array of strings (IDs). The JSON files use the v5 format (since we fetched v5 launches). The v4 endpoint returns the exact same JSON. If the Android app strictly expects v4 format, then either:
   - The JSON file serves both formats (simplest — the app may accept both), OR
   - A separate `launches_v4.json` file with transformed data is maintained.

4. **`/v4/roadster` query endpoint.** Unlike other resources, roadster returns a single object on `GET`. Its `POST /v4/roadster/query` endpoint wraps it as an array of 1 for paginated response. This matches the original API behavior.

5. **Empty arrays for `/past` and `/upcoming`.** If no launches match the filter criteria, return an empty array `[]` with HTTP 200, not a 404.

6. **`/v4/company` does NOT have a query endpoint.** This matches the original API.

7. **Trailing slashes.** `GET /v4/capsules/` should work identically to `GET /v4/capsules`. The handler trims trailing slashes via `strings.TrimRight(r.URL.Path, "/")` or by splitting and filtering empty parts.

8. **Sorting for /latest, /next, /past, /upcoming.** The Wayback data may not arrive in sorted order. The launch handler must sort appropriately for each sub-route:
   - `/latest`: sort by `date_unix` descending, take first
   - `/next`: filter `upcoming=true`, sort by `date_unix` ascending, take first
   - `/past`: filter `upcoming=false`, sort by `date_unix` descending
   - `/upcoming`: filter `upcoming=true`, sort by `date_unix` ascending

9. **Go `embed` package limitation.** `//go:embed data/*.json` matches files in the `data/` directory relative to the source file. The `handler.go` file (which contains the embed directive) must be in the `server/` package. The pattern `data/*.json` creates an embed.FS that maps files without the `data/` prefix. The `ReadFile` calls must use paths like `"data/launches_v5.json"` (relative to the embed directory, which IS the `server/` directory, so the embedded paths include the `data/` prefix). **Correction:** When using `//go:embed data/*.json`, the embedded paths are `data/launches_v5.json`, etc. So `ReadFile("data/launches_v5.json")` is correct.

---

## 12. Full File List (with paths relative to repo root)

```
api-server/
├── go.mod                              # module github.com/nisrulz/spacex-api-server, go 1.22
├── main.go                             # import "github.com/nisrulz/spacex-api-server/server"
└── server/
    ├── server.go                       # NewServer(), ListenAndServe(), corsMiddleware(), registerResourceRoutes()
    ├── handler.go                      //go:embed, init(), mustParse(), findByID(), handleQuery(), genericResourceHandler(), launchHandler(), individual handlers, handleCompany, handleRoadster, handleHealth
    └── data/                           # Embedded by go:embed (must be under server/)
        ├── launches_v5.json            # 205 records (Wayback)
        ├── launches_v4.json            # copy of launches_v5.json
        ├── capsules.json               # 25 records (Wayback)
        ├── crew.json                   # 30 records (Wayback)
        ├── roadster.json               # 1 object (Wayback)
        ├── rockets.json                # 4 records (Wayback)
        ├── ships.json                  # 29 records (Wayback)
        ├── company.json                # 1 object (generated)
        ├── cores.json                  # 10 records (generated)
        ├── dragons.json                # 4 records (generated)
        ├── history.json                # 10 records (generated)
        ├── landpads.json               # 5 records (generated)
        ├── launchpads.json             # 4 records (generated)
        ├── payloads.json               # 10 records (generated)
        └── starlink.json               # 5 records (generated)

core/network-retrofit/
├── build.gradle.kts                    # +buildFeatures.buildConfig, +buildConfigField API_BASE_URL
└── src/
    ├── main/kotlin/.../
    │   ├── di/NetworkModule.kt         # s/BASE_URL/BuildConfig.API_BASE_URL
    │   └── SpaceXLaunchesApi.kt        # remove BASE_URL constant
    └── test/
        ├── kotlin/.../
        │   └── SpaceXLaunchesApiTest.kt # Removed BASE_URL test (constant deleted)
        └── resources/
            └── response_items_list.json # replace with 205 records

.github/workflows/
└── maestro-tests.yml                   # +setup-go, +go build, +start server
```
