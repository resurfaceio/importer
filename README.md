# resurfaceio-importer
Import NDJSON into Resurface database

This command-line Java utility imports API calls (stored in [NDJSON format](https://resurface.io/json.html)) to a remote
Resurface database. This utility works with Resurface databases running on Docker or Kubernetes, and includes a few helpful
options for load testing. This utility is internally multi-threaded and is typically capable of saturating a gigabit
network connection.

[![CodeFactor](https://www.codefactor.io/repository/github/resurfaceio/importer/badge)](https://www.codefactor.io/repository/github/resurfaceio/importer)
[![License](https://img.shields.io/github/license/resurfaceio/importer)](https://github.com/resurfaceio/importer/blob/v3.3.x/LICENSE)
[![Contributing](https://img.shields.io/badge/contributions-welcome-green.svg)](https://github.com/resurfaceio/importer/blob/v3.3.x/CONTRIBUTING.md)

## Dependencies

* Java 11
* Maven
* [resurfaceio-ndjson](https://github.com/resurfaceio/ndjson)

## Building From Sources

```
git clone https://github.com/resurfaceio/importer.git resurfaceio-importer
cd resurfaceio-importer
mvn package
```

## Supported File Formats

This utility reads files in .ndjson.gz format exclusively. This is a compressed file format that can be exported from a
Resurface database, or generated using the [ndjson](https://github.com/resurfaceio/ndjson) library.

## Usage

Command-line parameters:
```
FILE: local .ndjson.gz file to import to remote database
HOST: machine name for remote database
PORT: network port for remote database
REPEAT: default is 'no', import file until process is cancelled
SATURATED_STOP: default is 'no', stop importing if database saturates
URL: override HOST and PORT with custom URL for remote database
```

Command-line example:
```
java -DFILE=$HOME/XXX.ndjson.gz -DHOST=localhost -DPORT=7701 -DREPEAT=yes -DSATURATED_STOP=yes -Xmx256M -jar target/main-jar-with-dependencies.jar
```

---
<small>&copy; 2016-2022 <a href="https://resurface.io">Resurface Labs Inc.</a></small>
