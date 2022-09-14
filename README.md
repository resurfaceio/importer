# resurfaceio-importer
Import NDJSON into Resurface database

## Supported file formats

* .ndjson.gz - Newline Delimited JSON with GZIP compression

Each line in the input file is parsed as a Resurface message:
https://resurface.io/json.html

## System requirements

* Java 17
* Maven

## Building from sources

```
git clone https://github.com/resurfaceio/importer.git resurfaceio-importer
cd resurfaceio-importer
mvn package
```

## Importing local file

```
java -DFILE=$HOME/XXX.ndjson.gz -DHOST=localhost -DREPEAT=yes -Xmx256M -jar target/main-jar-with-dependencies.jar
```
