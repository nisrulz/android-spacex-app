#!/usr/bin/env bash

set -e # Exit immediately if a command exits with a non-zero status.

if [[ -x "$HOME/.maestro/bin/maestro" ]]; then
  exit 0
fi

# Install Maestro CLI
curl -Ls "https://get.maestro.mobile.dev" | bash
