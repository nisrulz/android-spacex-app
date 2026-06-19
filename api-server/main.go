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
