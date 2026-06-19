package server

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestFindByID(t *testing.T) {
	data := []any{
		map[string]any{"id": "abc", "name": "first"},
		map[string]any{"id": "xyz", "name": "second"},
	}

	got := findByID(data, "abc")
	if got == nil {
		t.Fatal("expected to find id=abc, got nil")
	}
	m := got.(map[string]any)
	if m["name"] != "first" {
		t.Errorf("expected name=first, got %v", m["name"])
	}

	got = findByID(data, "nonexistent")
	if got != nil {
		t.Errorf("expected nil for nonexistent id, got %v", got)
	}
}

func TestHandleQuery(t *testing.T) {
	data := make([]any, 50)
	for i := range data {
		data[i] = map[string]any{"id": i, "val": i * 2}
	}

	tests := []struct {
		name        string
		body        string
		wantDocs    int
		wantTotal   int
		wantPage    int
		wantPages   int
		wantHasNext bool
		wantHasPrev bool
	}{
		{"default pagination", `{}`, 10, 50, 1, 5, true, false},
		{"limit 5 offset 0", `{"options":{"limit":5}}`, 5, 50, 1, 10, true, false},
		{"limit 10 offset 10", `{"options":{"limit":10,"offset":10}}`, 10, 50, 2, 5, true, true},
		{"offset past end", `{"options":{"limit":10,"offset":100}}`, 0, 50, 11, 5, false, true},
		{"limit exceeds total", `{"options":{"limit":200}}`, 50, 50, 1, 1, false, false},
		{"empty body", ``, 10, 50, 1, 5, true, false},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			req := httptest.NewRequest(http.MethodPost, "/query", strings.NewReader(tt.body))
			req.Header.Set("Content-Type", "application/json")
			w := httptest.NewRecorder()

			handleQuery(w, req, data)
			resp := w.Result()
			if resp.StatusCode != http.StatusOK {
				t.Fatalf("expected 200, got %d", resp.StatusCode)
			}

			var qr queryResponse
			if err := json.NewDecoder(resp.Body).Decode(&qr); err != nil {
				t.Fatalf("failed to decode response: %v", err)
			}

			if len(qr.Docs) != tt.wantDocs {
				t.Errorf("docs: got %d, want %d", len(qr.Docs), tt.wantDocs)
			}
			if qr.TotalDocs != tt.wantTotal {
				t.Errorf("totalDocs: got %d, want %d", qr.TotalDocs, tt.wantTotal)
			}
			if qr.Page != tt.wantPage {
				t.Errorf("page: got %d, want %d", qr.Page, tt.wantPage)
			}
			if qr.TotalPages != tt.wantPages {
				t.Errorf("totalPages: got %d, want %d", qr.TotalPages, tt.wantPages)
			}
			if qr.HasNextPage != tt.wantHasNext {
				t.Errorf("hasNextPage: got %v, want %v", qr.HasNextPage, tt.wantHasNext)
			}
			if qr.HasPrevPage != tt.wantHasPrev {
				t.Errorf("hasPrevPage: got %v, want %v", qr.HasPrevPage, tt.wantHasPrev)
			}
		})
	}
}

func TestHandleQueryMethodNotAllowed(t *testing.T) {
	req := httptest.NewRequest(http.MethodGet, "/query", nil)
	w := httptest.NewRecorder()
	handleQuery(w, req, []any{})

	if w.Result().StatusCode != http.StatusMethodNotAllowed {
		t.Errorf("expected 405 for GET, got %d", w.Result().StatusCode)
	}
}

func TestGenericResourceHandler(t *testing.T) {
	data := []any{
		map[string]any{"id": "a1", "label": "alpha"},
		map[string]any{"id": "b2", "label": "beta"},
	}
	h := genericResourceHandler(data)

	t.Run("list all", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v4/testresources", nil)
		w := httptest.NewRecorder()
		h(w, req)

		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var result []any
		json.NewDecoder(w.Result().Body).Decode(&result)
		if len(result) != 2 {
			t.Errorf("expected 2 items, got %d", len(result))
		}
	})

	t.Run("find by existing ID", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v4/testresources/a1", nil)
		w := httptest.NewRecorder()
		h(w, req)

		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var item map[string]any
		json.NewDecoder(w.Result().Body).Decode(&item)
		if item["label"] != "alpha" {
			t.Errorf("expected label=alpha, got %v", item["label"])
		}
	})

	t.Run("find by nonexistent ID", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v4/testresources/zzz", nil)
		w := httptest.NewRecorder()
		h(w, req)

		if w.Result().StatusCode != http.StatusNotFound {
			t.Errorf("expected 404, got %d", w.Result().StatusCode)
		}
	})

	t.Run("query", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodPost, "/v4/testresources/query", strings.NewReader(`{"options":{"limit":1}}`))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		h(w, req)

		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var qr queryResponse
		json.NewDecoder(w.Result().Body).Decode(&qr)
		if len(qr.Docs) != 1 {
			t.Errorf("expected 1 doc, got %d", len(qr.Docs))
		}
		if qr.TotalDocs != 2 {
			t.Errorf("expected totalDocs=2, got %d", qr.TotalDocs)
		}
	})

	t.Run("method not allowed", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodDelete, "/v4/testresources", nil)
		w := httptest.NewRecorder()
		h(w, req)

		if w.Result().StatusCode != http.StatusMethodNotAllowed {
			t.Errorf("expected 405, got %d", w.Result().StatusCode)
		}
	})
}

func TestLaunchHandler(t *testing.T) {
	data := []any{
		map[string]any{"id": "1", "name": "Old Launch", "date_unix": 1000.0, "upcoming": false},
		map[string]any{"id": "2", "name": "Middle Launch", "date_unix": 2000.0, "upcoming": false},
		map[string]any{"id": "3", "name": "Latest Launch", "date_unix": 3000.0, "upcoming": false},
		map[string]any{"id": "4", "name": "Next Upcoming", "date_unix": 4000.0, "upcoming": true},
		map[string]any{"id": "5", "name": "Far Upcoming", "date_unix": 5000.0, "upcoming": true},
	}
	h := launchHandler(data)

	t.Run("list all", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var result []any
		json.NewDecoder(w.Result().Body).Decode(&result)
		if len(result) != 5 {
			t.Errorf("expected 5 items, got %d", len(result))
		}
	})

	t.Run("latest", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/latest", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var item map[string]any
		json.NewDecoder(w.Result().Body).Decode(&item)
		if item["name"] != "Far Upcoming" {
			t.Errorf("expected 'Far Upcoming' (max date_unix), got %v", item["name"])
		}
	})

	t.Run("next", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/next", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var item map[string]any
		json.NewDecoder(w.Result().Body).Decode(&item)
		if item["name"] != "Next Upcoming" {
			t.Errorf("expected 'Next Upcoming', got %v", item["name"])
		}
	})

	t.Run("past", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/past", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var result []any
		json.NewDecoder(w.Result().Body).Decode(&result)
		if len(result) != 3 {
			t.Errorf("expected 3 past launches, got %d", len(result))
		}
	})

	t.Run("upcoming", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/upcoming", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var result []any
		json.NewDecoder(w.Result().Body).Decode(&result)
		if len(result) != 2 {
			t.Errorf("expected 2 upcoming launches, got %d", len(result))
		}
	})

	t.Run("by ID", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/1", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var item map[string]any
		json.NewDecoder(w.Result().Body).Decode(&item)
		if item["name"] != "Old Launch" {
			t.Errorf("expected 'Old Launch', got %v", item["name"])
		}
	})

	t.Run("by nonexistent ID", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/zzz", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusNotFound {
			t.Errorf("expected 404, got %d", w.Result().StatusCode)
		}
	})

	t.Run("query", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodPost, "/v5/launches/query", strings.NewReader(`{"options":{"limit":2}}`))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var qr queryResponse
		json.NewDecoder(w.Result().Body).Decode(&qr)
		if len(qr.Docs) != 2 {
			t.Errorf("expected 2 docs, got %d", len(qr.Docs))
		}
	})

	t.Run("method not allowed", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodPut, "/v5/launches", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusMethodNotAllowed {
			t.Errorf("expected 405, got %d", w.Result().StatusCode)
		}
	})
}

func TestLaunchHandlerEmpty(t *testing.T) {
	h := launchHandler([]any{})

	t.Run("latest on empty", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/latest", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusNotFound {
			t.Errorf("expected 404 for empty data, got %d", w.Result().StatusCode)
		}
	})

	t.Run("next on empty", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/v5/launches/next", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusNotFound {
			t.Errorf("expected 404 for empty data, got %d", w.Result().StatusCode)
		}
	})
}

func TestSingleObjectHandlers(t *testing.T) {
	t.Run("company", func(t *testing.T) {
		h := companyHandler()
		req := httptest.NewRequest(http.MethodGet, "/v4/company", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var item map[string]any
		json.NewDecoder(w.Result().Body).Decode(&item)
		if item["name"] != "SpaceX" {
			t.Errorf("expected name=SpaceX, got %v", item["name"])
		}
	})

	t.Run("company method not allowed", func(t *testing.T) {
		h := companyHandler()
		req := httptest.NewRequest(http.MethodPost, "/v4/company", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusMethodNotAllowed {
			t.Errorf("expected 405, got %d", w.Result().StatusCode)
		}
	})

	t.Run("roadster", func(t *testing.T) {
		h := roadsterHandler()
		req := httptest.NewRequest(http.MethodGet, "/v4/roadster", nil)
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
	})

	t.Run("roadster query", func(t *testing.T) {
		h := roadsterQueryHandler()
		req := httptest.NewRequest(http.MethodPost, "/v4/roadster/query", strings.NewReader(`{}`))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		h(w, req)
		if w.Result().StatusCode != http.StatusOK {
			t.Fatalf("expected 200, got %d", w.Result().StatusCode)
		}
		var qr queryResponse
		json.NewDecoder(w.Result().Body).Decode(&qr)
		if qr.TotalDocs != 1 {
			t.Errorf("expected totalDocs=1 for roadster query, got %d", qr.TotalDocs)
		}
	})
}

func TestHealthHandler(t *testing.T) {
	h := healthHandler()
	req := httptest.NewRequest(http.MethodGet, "/healthz", nil)
	w := httptest.NewRecorder()
	h(w, req)

	if w.Result().StatusCode != http.StatusOK {
		t.Fatalf("expected 200, got %d", w.Result().StatusCode)
	}
	var body map[string]string
	json.NewDecoder(w.Result().Body).Decode(&body)
	if body["status"] != "ok" {
		t.Errorf("expected status=ok, got %v", body["status"])
	}
}
