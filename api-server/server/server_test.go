package server

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestCORSMiddleware(t *testing.T) {
	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
	})

	t.Run("adds CORS headers", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/", nil)
		w := httptest.NewRecorder()
		corsMiddleware(handler).ServeHTTP(w, req)

		resp := w.Result()
		if resp.Header.Get("Access-Control-Allow-Origin") != "*" {
			t.Error("missing Access-Control-Allow-Origin: *")
		}
		if resp.Header.Get("Access-Control-Allow-Methods") != "GET, POST, OPTIONS" {
			t.Error("missing or wrong Access-Control-Allow-Methods")
		}
		if resp.Header.Get("Access-Control-Allow-Headers") != "Content-Type" {
			t.Error("missing or wrong Access-Control-Allow-Headers")
		}
	})

	t.Run("handles OPTIONS request", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodOptions, "/", nil)
		w := httptest.NewRecorder()
		corsMiddleware(handler).ServeHTTP(w, req)

		resp := w.Result()
		if resp.StatusCode != http.StatusOK {
			t.Errorf("expected 200 for OPTIONS, got %d", resp.StatusCode)
		}
	})

	t.Run("passes through GET request", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/", nil)
		w := httptest.NewRecorder()
		corsMiddleware(handler).ServeHTTP(w, req)

		resp := w.Result()
		if resp.StatusCode != http.StatusOK {
			t.Errorf("expected 200, got %d", resp.StatusCode)
		}
	})
}

func TestServerEndpoints(t *testing.T) {
	// Start a test server using the real ListenAndServe setup
	mux := http.NewServeMux()

	mux.HandleFunc("/v5/launches/", handleV5Launches)
	mux.HandleFunc("/v5/launches", handleV5Launches)
	mux.HandleFunc("/v4/launches/", handleV4Launches)
	mux.HandleFunc("/v4/launches", handleV4Launches)

	mux.HandleFunc("/v4/capsules/", handleCapsules)
	mux.HandleFunc("/v4/capsules", handleCapsules)
	mux.HandleFunc("/v4/cores/", handleCores)
	mux.HandleFunc("/v4/cores", handleCores)
	mux.HandleFunc("/v4/crew/", handleCrew)
	mux.HandleFunc("/v4/crew", handleCrew)
	mux.HandleFunc("/v4/dragons/", handleDragons)
	mux.HandleFunc("/v4/dragons", handleDragons)
	mux.HandleFunc("/v4/history/", handleHistory)
	mux.HandleFunc("/v4/history", handleHistory)
	mux.HandleFunc("/v4/landpads/", handleLandpads)
	mux.HandleFunc("/v4/landpads", handleLandpads)
	mux.HandleFunc("/v4/launchpads/", handleLaunchpads)
	mux.HandleFunc("/v4/launchpads", handleLaunchpads)
	mux.HandleFunc("/v4/payloads/", handlePayloads)
	mux.HandleFunc("/v4/payloads", handlePayloads)
	mux.HandleFunc("/v4/rockets/", handleRockets)
	mux.HandleFunc("/v4/rockets", handleRockets)
	mux.HandleFunc("/v4/ships/", handleShips)
	mux.HandleFunc("/v4/ships", handleShips)
	mux.HandleFunc("/v4/starlink/", handleStarlink)
	mux.HandleFunc("/v4/starlink", handleStarlink)

	mux.HandleFunc("/v4/company", handleCompany)
	mux.HandleFunc("/v4/roadster", handleRoadster)
	mux.HandleFunc("/v4/roadster/query", handleRoadsterQuery)

	mux.HandleFunc("/healthz", handleHealth)

	ts := httptest.NewServer(corsMiddleware(mux))
	defer ts.Close()

	resourcePaths := []string{
		"/v5/launches",
		"/v4/launches",
		"/v4/capsules",
		"/v4/cores",
		"/v4/crew",
		"/v4/dragons",
		"/v4/history",
		"/v4/landpads",
		"/v4/launchpads",
		"/v4/payloads",
		"/v4/rockets",
		"/v4/ships",
		"/v4/starlink",
		"/v4/company",
		"/v4/roadster",
		"/healthz",
	}

	for _, path := range resourcePaths {
		t.Run("GET "+path, func(t *testing.T) {
			resp, err := http.Get(ts.URL + path)
			if err != nil {
				t.Fatal(err)
			}
			defer resp.Body.Close()
			if resp.StatusCode != http.StatusOK {
				t.Errorf("expected 200, got %d for %s", resp.StatusCode, path)
			}
		})
	}

	specialPaths := []string{
		"/v5/launches/latest",
		"/v5/launches/next",
		"/v5/launches/past",
		"/v5/launches/upcoming",
		"/v4/launches/latest",
		"/v4/launches/next",
	}

	for _, path := range specialPaths {
		t.Run("GET "+path, func(t *testing.T) {
			resp, err := http.Get(ts.URL + path)
			if err != nil {
				t.Fatal(err)
			}
			defer resp.Body.Close()
			if resp.StatusCode != http.StatusOK {
				t.Errorf("expected 200, got %d for %s", resp.StatusCode, path)
			}
		})
	}

	t.Run("POST /v5/launches/query", func(t *testing.T) {
		resp, err := http.Post(ts.URL+"/v5/launches/query", "application/json", nil)
		if err != nil {
			t.Fatal(err)
		}
		defer resp.Body.Close()
		if resp.StatusCode != http.StatusOK {
			t.Errorf("expected 200, got %d", resp.StatusCode)
		}
	})

	t.Run("GET nonexistent ID returns 404", func(t *testing.T) {
		resp, err := http.Get(ts.URL + "/v4/capsules/doesnotexist")
		if err != nil {
			t.Fatal(err)
		}
		defer resp.Body.Close()
		if resp.StatusCode != http.StatusNotFound {
			t.Errorf("expected 404, got %d", resp.StatusCode)
		}
	})

	t.Run("CORS headers on all responses", func(t *testing.T) {
		resp, err := http.Get(ts.URL + "/v5/launches")
		if err != nil {
			t.Fatal(err)
		}
		defer resp.Body.Close()
		if resp.Header.Get("Access-Control-Allow-Origin") != "*" {
			t.Error("missing CORS header")
		}
	})
}
