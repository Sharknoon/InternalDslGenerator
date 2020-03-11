# Internal DSL Generator UI

This is a little webinterface for [etgramli](https://github.com/etgramli)s [AntlrTest Internal Java DSL Generator](https://github.com/etgramli/AntlrTest)

[![buddy pipeline](https://app.buddy.works/sharknoon/internaldslgenerator/pipelines/pipeline/244367/badge.svg?token=bc546db16e7ab333d81c90ea1cccd24d5b2d6d924e81303b82a7816e8b269b52 "buddy pipeline")](https://app.buddy.works/sharknoon/internaldslgenerator/pipelines/pipeline/244367)

## Build and Run local
Run
```
git clone https://github.com/Sharknoon/InternalDslGenerator.git
cd InternalDslGenerator
set GITHUB_USERNAME=YOUR_GITHUB_USERNAME
set GITHUB_TOKEN=YOUR_GITHUB_ACCESS_TOKEN
mvn spring-boot:run -s settings.xml
```
and visit [localhost:8080](http://localhost:8080)

> Be sure to grant at least the scope ```read:packages``` to the access token!

## Run prebuilt docker image
Run
```
docker run -p 8080:8080 sharknoon/internal-dsl-generator
```
and visit [localhost:8080](http://localhost:8080)

## Build docker image
Run
```
git clone https://github.com/Sharknoon/InternalDslGenerator.git
cd InternalDslGenerator
set GITHUB_USERNAME=YOUR_GITHUB_USERNAME
set GITHUB_TOKEN=YOUR_GITHUB_ACCESS_TOKEN
mvn package -P production -s settings.xml
docker build -t YOUR_DOCKER_USERNAME/internal-dsl-generator:latest .
```
and deploy it to a repository near you

> Be sure to grant at least the scope ```read:packages``` to the access token!