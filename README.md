# Internal DSL Generator UI
This is a litte Webinterface for [etgramli](https://github.com/etgramli)s [AntlrTest Internal Java DSL Generator](https://github.com/etgramli/AntlrTest)

## Run local
Run
```
git clone https://github.com/Sharknoon/InternalDslGenerator.git
cd InternalDslGenerator
mvn spring-boot:run
```
and visit [localhost:8080](http://localhost:8080)

## Run docker
Run
```
git clone https://github.com/Sharknoon/InternalDslGenerator.git
cd InternalDslGenerator
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
