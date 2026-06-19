package server

import (
	"log"
	"net/http"
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

func registerResourceRoutes(mux *http.ServeMux, prefix string, handler http.HandlerFunc) {
	mux.HandleFunc(prefix+"/", handler)
	mux.HandleFunc(prefix, handler)
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

// ListenAndServeTLS creates the mux, registers all routes, wraps with CORS, and listens on TLS.
func ListenAndServeTLS(addr, certFile, keyFile string) error {
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

	log.Printf("Routes registered (TLS):")
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

	return http.ListenAndServeTLS(addr, certFile, keyFile, corsMiddleware(mux))
}
