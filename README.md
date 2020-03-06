# Internal DSL Generator UI
This is a litte Webinterface for [etgramli](https://github.com/etgramli)s [AntlrTest Internal Java DSL Generator](https://github.com/etgramli/AntlrTest)

## Build and Run local
Run
```
git clone https://github.com/Sharknoon/InternalDslGenerator.git
cd InternalDslGenerator
mvn spring-boot:run -Dserver.id=github -Dserver.username=YOUR_GITHUB_USERNAME -Dserver.password=YOUR_GITHUB_ACCESS_TOKEN
```
and visit [localhost:8080](http://localhost:8080)

Be sure to grant the access token the scope ```read:packages```!

## Run prebuilt docker
Run
```
docker run -p 8080:8080 sharknoon/internal-dsl-generator
```
and visit [localhost:8080](http://localhost:8080)

## Build yourself
Run
```
git clone https://github.com/Sharknoon/InternalDslGenerator.git
cd InternalDslGenerator
docker build -t sharknoon/internal-dsl-generator:latest .
```
and deploy it to a repository near you
