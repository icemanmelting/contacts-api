#!/bin/sh
rm contacts-api.zip
lein clean

if lein uberjar; then
    zip -r contacts-api.zip Dockerfile* ./resources/* ./target/uberjar/contacts-api-standalone.jar
    echo "ZIP created successfully"
else
    echo "Cannot create ZIP"
fi
