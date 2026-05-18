#!/bin/bash

# This wrapper script uses the system gradle directly
cd "$(dirname "$0")"

# Find gradle in PATH
if command -v gradle &> /dev/null; then
    GRADLE_CMD="gradle"
else
    echo "Error: gradle not found in PATH"
    exit 1
fi

# Run gradle with the provided arguments
exec $GRADLE_CMD "$@"
