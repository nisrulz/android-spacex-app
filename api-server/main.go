package main

import (
	"flag"
	"log"
	"os"

	"github.com/nisrulz/spacex-api-server/server"
)

func main() {
	port := flag.String("port", "8443", "port to listen on")
	certFile := flag.String("cert", "certs/server.crt", "TLS certificate file")
	keyFile := flag.String("key", "certs/server.key", "TLS private key file")
	flag.Parse()

	if _, err := os.Stat(*certFile); err == nil {
		if _, err := os.Stat(*keyFile); err == nil {
			log.Printf("SpaceX API server starting on :%s (HTTPS)", *port)
			log.Fatal(server.ListenAndServeTLS(":"+*port, *certFile, *keyFile))
			return
		}
	}
	log.Printf("SpaceX API server starting on :%s (HTTP — certs/server.crt/key not found)", *port)
	log.Fatal(server.ListenAndServe(":" + *port))
}
