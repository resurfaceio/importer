# resurfaceio-importer
Import NDJSON into Resurface database

This command-line Java utility imports API calls (stored in [NDJSON format](https://resurface.io/json.html)) to a remote
Resurface database. This utility works with Resurface databases running on Docker or Kubernetes, and includes a few helpful
options for load testing. This utility is multi-threaded and is typically capable of saturating a gigabit
network connection.

[![CodeFactor](https://www.codefactor.io/repository/github/resurfaceio/importer/badge)](https://www.codefactor.io/repository/github/resurfaceio/importer)
[![License](https://img.shields.io/github/license/resurfaceio/importer)](https://github.com/resurfaceio/importer/blob/v3.3.x/LICENSE)
[![Contributing](https://img.shields.io/badge/contributions-welcome-green.svg)](https://github.com/resurfaceio/importer/blob/v3.3.x/CONTRIBUTING.md)

## Dependencies

* Java 11
* Maven
* [resurfaceio-ndjson](https://github.com/resurfaceio/ndjson) (included)

## Usage

[Download executable jar](https://dl.cloudsmith.io/public/resurfacelabs/public/maven/io/resurface/resurfaceio-importer/3.3.3/resurfaceio-importer-3.3.3.jar)

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
java -DFILE=$HOME/XXX.ndjson.gz -DHOST=localhost -DPORT=7701 -DREPEAT=no -DSATURATED_STOP=no -Xmx256M -jar $HOME/Downloads/resurfaceio-importer-3.3.3.jar
```

## Installing with Maven

⚠️ We publish our official binaries on [CloudSmith](https://cloudsmith.com) rather than Maven Central, because CloudSmith
is awesome.

If you want to call this utility from your own Java application, add these sections to `pom.xml` to install:

```xml
<dependency>
    <groupId>io.resurface</groupId>
    <artifactId>resurfaceio-importer</artifactId>
    <version>3.3.3</version>
</dependency>
```

```xml
<repositories>
    <repository>
        <id>resurfacelabs-public</id>
        <url>https://dl.cloudsmith.io/public/resurfacelabs/public/maven/</url>
        <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </releases>
    </repository>
</repositories>
```

## Supported File Formats

This utility reads files in .ndjson.gz format exclusively. This is a compressed file format that can be exported from a
Resurface database, or generated using the [ndjson](https://github.com/resurfaceio/ndjson) library.

---
<small>&copy; 2016-2022 <a href="https://resurface.io">Resurface Labs Inc.</a></small>
