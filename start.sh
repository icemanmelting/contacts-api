#!/bin/sh
docker run -e CONTACTS_API_ENV=default -it --rm -p 8080:8080  contacts-api