# resurfaceio-importer

## Supported file formats

* .ndjson - Newline Delimited JSON (http://ndjson.org)
* .ndjson.gz - Newline Delimited JSON with GZIP compression

Each line in the input file is parsed as a Resurface message:
https://resurface.io/json.html

## System requirements

* Java 8 or 11
* Maven

## Building from sources

```
git clone https://github.com/resurfaceio/importer.git resurfaceio-importer
cd resurfaceio-importer
mvn package
```

## Importing local file

```
FILE=~/Dropbox/datasets/website.ndjson.gz java -Xmx512M -jar target/main-jar-with-dependencies.jar
```

## Creating compressed files

```
gzip -k [file.ndjson]
```