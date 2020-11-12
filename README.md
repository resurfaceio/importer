# resurfaceio-importer

## System requirements

* Java 8 or 11
* Maven

## Building and running tests

```
git clone https://github.com/resurfaceio/importer.git resurfaceio-importer
cd resurfaceio-importer
mvn package
```

## Importing local file

```
FILE=/Users/robfromboulder/Downloads/2020-10-05.json java -Xmx512M -jar target/main-jar-with-dependencies.jar
```
