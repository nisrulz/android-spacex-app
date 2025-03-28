#!/usr/bin/env bash

set -e # Exit immediately if a command exits with a non-zero status.

# Install Maestro CLI
curl -Ls "https://get.maestro.mobile.dev" | bash
export PATH="$HOME/.maestro/bin:$PATH"
export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED=true
export MAESTRO_CLI_NO_ANALYTICS=1