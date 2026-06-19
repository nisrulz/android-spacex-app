package server

import (
	"embed"
	"encoding/json"
	"log"
	"math"
	"net/http"
	"slices"
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
	Docs        []any `json:"docs"`
	TotalDocs   int   `json:"totalDocs"`
	Limit       int   `json:"limit"`
	Offset      int   `json:"offset"`
	Page        int   `json:"page"`
	TotalPages  int   `json:"totalPages"`
	HasPrevPage bool  `json:"hasPrevPage"`
	HasNextPage bool  `json:"hasNextPage"`
	PrevPage    *int  `json:"prevPage"`
	NextPage    *int  `json:"nextPage"`
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

	company  any
	roadster any
)

var resourceNames = []string{
	"capsules", "cores", "crew", "dragons", "history",
	"landpads", "launchpads", "payloads", "rockets", "ships", "starlink",
}

// ─── Handler variables (assigned in init) ─────────────────────────────────

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
	handleCompany    http.HandlerFunc
	handleRoadster   http.HandlerFunc
	handleRoadsterQuery http.HandlerFunc
	handleHealth     http.HandlerFunc
)

func mustParse(path string, target any) {
	data, err := dataFS.ReadFile(path)
	if err != nil {
		log.Fatalf("Failed to read embedded %s: %v", path, err)
	}
	if err := json.Unmarshal(data, target); err != nil {
		log.Fatalf("Failed to parse %s: %v", path, err)
	}
}

func init() {
	// Load data
	mustParse("data/launches_v5.json", &launchesV5)
	mustParse("data/launches_v4.json", &launchesV4)
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
	mustParse("data/company.json", &company)
	mustParse("data/roadster.json", &roadster)

	log.Printf("Loaded %d launches (v5), %d launches (v4)", len(launchesV5), len(launchesV4))
	log.Printf("Loaded: capsules=%d, cores=%d, crew=%d, dragons=%d, history=%d, landpads=%d, launchpads=%d, payloads=%d, rockets=%d, ships=%d, starlink=%d",
		len(capsules), len(cores), len(crew), len(dragons), len(history),
		len(landpads), len(launchpads), len(payloads), len(rockets), len(ships), len(starlink))

	// Create handlers
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
	handleCompany = companyHandler()
	handleRoadster = roadsterHandler()
	handleRoadsterQuery = roadsterQueryHandler()
	handleHealth = healthHandler()
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

// ─── Query handler ─────────────────────────────────────────────────────────

func handleQuery(w http.ResponseWriter, r *http.Request, data []any) {
	if r.Method != http.MethodPost {
		writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
		return
	}

	var body queryBody
	json.NewDecoder(r.Body).Decode(&body)

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

// ─── Generic resource handler ──────────────────────────────────────────────

func genericResourceHandler(data []any) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		parts := splitPath(r.URL.Path)

		switch r.Method {
		case http.MethodGet:
			if len(parts) <= 2 {
				// GET /v4/{resource}
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

// splitPath splits a URL path like "/v4/capsules/abc123" into ["v4", "capsules", "abc123"].
func splitPath(urlPath string) []string {
	var parts []string
	for _, p := range strings.Split(strings.Trim(urlPath, "/"), "/") {
		if p != "" {
			parts = append(parts, p)
		}
	}
	return parts
}

// ─── Launch handler (special: sub-routes /latest, /next, /past, /upcoming) ─

func launchHandler(data []any) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		parts := splitPath(r.URL.Path)

		if r.Method == http.MethodGet {
			if len(parts) <= 2 {
				// GET /v{5,4}/launches — list all
				writeJSON(w, http.StatusOK, data)
				return
			}

			sub := parts[2]
			switch sub {
			case "latest":
				if len(data) == 0 {
					writeError(w, http.StatusNotFound, "No launches")
					return
				}
				latest := data[0]
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
					if ta < tb {
						return -1
					}
					if ta > tb {
						return 1
					}
					return 0
				})
				writeJSON(w, http.StatusOK, upcoming[0])

			case "past":
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
					if ta > tb {
						return -1
					}
					if ta < tb {
						return 1
					}
					return 0
				})
				writeJSON(w, http.StatusOK, past)

			case "upcoming":
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
					if ta < tb {
						return -1
					}
					if ta > tb {
						return 1
					}
					return 0
				})
				writeJSON(w, http.StatusOK, upcoming)

			default:
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

// ─── Single-object handlers ──────────────────────────────────────────────

func companyHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet {
			writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
			return
		}
		writeJSON(w, http.StatusOK, company)
	}
}

func roadsterHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet {
			writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
			return
		}
		writeJSON(w, http.StatusOK, roadster)
	}
}

func roadsterQueryHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost {
			writeError(w, http.StatusMethodNotAllowed, "Method not allowed")
			return
		}
		roadsterArr := []any{roadster}
		handleQuery(w, r, roadsterArr)
	}
}

func healthHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
	}
}
