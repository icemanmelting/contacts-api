#!/bin/sh
lein clean

if lein uberjar; then
    docker build -t contacts-api .
    echo "Docker image build successfully. Running..."
    docker run -e CONTACTS_API_ENV=default  -it --rm -p 8080:8080  contacts-api
else
    echo "Cannot compile"
fi
