#!/usr/bin/env bash

set -e # Exit immediately if a command exits with a non-zero status.

# Run Maestro Tests
export APP_ID="com.nisrulz.example.spacexapi"
maestro test -e APP_ID=$APP_ID .maestro/
