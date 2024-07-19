# Overview

## What

This project crawls through wikipedia articles, and stores their title, url and links to other articles in a database.

## Why

Mainly as a way too learn Kotlin.
Maybe I will do something with this data in the future

# Running it

The main application requires a MariaDB database to be running. It will create the schema automatically.

For development, or just running locally, there is a docker compose config under mariadb/docker-compose.yml.  
This will just run a MariaDB container with the correct settings.

For actual deployment in production, you have to first build a docker image via the included Dockerfile.
Then you can use the ./docker-compose.yml to run both the crawler itself and the database.
Make sure you give the image the correct name and tag ("wiki-crawler:dev" by default). 