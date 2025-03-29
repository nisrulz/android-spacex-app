#!/usr/bin/env bash

set -e # Exit immediately if a command exits with a non-zero status.

# Run Maestro Tests
export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED=true
export MAESTRO_CLI_NO_ANALYTICS=1
export PATH="$PATH":"$HOME/.maestro/bin"

export APP_ID="com.nisrulz.example.spacexapi"

maestro test -e APP_ID=$APP_ID .maestro/ --format html

# Get the current timestamp (ddmmyy_hhmmss)
timestamp=$(date +'%d%m%y_%H%M%S')

# Rename the output file with a timestamp
mv report.html "maestro_test_result_${timestamp}.html"