# resurfaceio-importer
Import NDJSON into Resurface database

This command-line utility imports API calls (stored in NDJSON format) to a remote Resurface database.
This utility works with Resurface databases running on Docker or Kubernetes, and includes a few helpful
options for load testing. This utility acts as a remote logger, using the same network interface and
JSON format as all of our open-source loggers.

## System Requirements

* Java 11
* Maven

## Building From Sources

```
git clone https://github.com/resurfaceio/importer.git resurfaceio-importer
cd resurfaceio-importer
mvn package
```

## Importing Local File

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

⚠️ The importer is single-threaded, but typically fast enough to saturate a gigabit ethernet connection.

## Supported File Formats

This utility reads files in .ndjson.gz format exclusively.

This is a compressed file format where each line is a JSON array representing a single API call.

JSON format and examples are provided here: https://resurface.io/json.html

---
<small>&copy; 2016-2022 <a href="https://resurface.io">Resurface Labs Inc.</a></small>
