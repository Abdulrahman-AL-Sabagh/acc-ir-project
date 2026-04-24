#!/bin/sh
# get dir of this script
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
exec java -cp "$SCRIPT_DIR/bin" ir.MiniLang $@
