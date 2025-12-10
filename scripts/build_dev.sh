#!/bin/bash

set -e  # Exit on any error
set -u  # Treat unset variables as an error

echo "[SCRIPT] Reading environment variables..."
source ./read_env.sh
load_env ../.env
echo "[SCRIPT] Done."

echo "[SCRIPT] Building plugin jar..."
gradle -p .. clean build
echo "[SCRIPT] Built"

echo "[SCRIPT] ✅  Public plugin build script successful at '$BUILD_DESTINATION_DIRECTORY'"