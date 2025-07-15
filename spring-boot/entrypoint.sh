#!/bin/sh
echo "$GOOGLE_APPLICATION_CREDENTIALS_JSON" > /tmp/key.json
export GOOGLE_APPLICATION_CREDENTIALS=/tmp/key.json

exec java -jar app.jar