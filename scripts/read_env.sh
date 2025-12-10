#!/bin/bash

# Function to load environment variables from a given file
load_env() {
    local filename="$1"

    # If no filename is provided, default to .env in the script directory
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    local env_file="${filename:-$script_dir/.env}"

    if [ -f "$env_file" ]; then
        export $(grep -v '^#' "$env_file" | xargs)
        echo "Environment variables loaded from $env_file"
    else
        echo "No .env file found at $env_file" >&2
        return 1
    fi
}