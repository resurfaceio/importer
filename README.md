# resurfaceio-importer
Import NDJSON into Resurface database

This open source Java utility imports API calls (stored in [NDJSON format](https://resurface.io/json.html)) to
a remote Resurface database. This command-line utility works with Resurface databases on Docker or Kubernetes,
and includes a few helpful options for load testing.

[![CodeFactor](https://www.codefactor.io/repository/github/resurfaceio/importer/badge)](https://www.codefactor.io/repository/github/resurfaceio/importer)
[![License](https://img.shields.io/github/license/resurfaceio/importer)](https://github.com/resurfaceio/importer/blob/v3.5.x/LICENSE)
[![Contributing](https://img.shields.io/badge/contributions-welcome-green.svg)](https://github.com/resurfaceio/importer/blob/v3.5.x/CONTRIBUTING.md)

## Usage

Download executable jar:
```
wget https://dl.cloudsmith.io/public/resurfacelabs/public/maven/io/resurface/resurfaceio-importer/3.5.2/resurfaceio-importer-3.5.2.jar
```

Importing a local file:
```
java -DFILE=XXX.ndjson.gz -DHOST=localhost -DPORT=7701 -DLIMIT_MESSAGES=0 -DLIMIT_MILLIS=0 -DREPEAT=no -DSATURATED_STOP=no -Xmx512M -jar resurfaceio-importer-3.5.2.jar
```

## Environment Variables

```
FILE: local .ndjson.gz file to import to remote database
HOST: machine name for remote database
PORT: network port for remote database

LIMIT_MESSAGES: default is '0' (unlimited), quit after this many messages
LIMIT_MILLIS: default is '0' (unlimited), quit after this many milliseconds
REPEAT: default is 'no', import file until process is cancelled
SATURATED_STOP: default is 'no', quit if database is saturated
URL: override HOST and PORT with custom URL for remote database
```

## Dependencies

* Java 17
* [resurfaceio/ndjson](https://github.com/resurfaceio/ndjson)

## Installing with Maven

⚠️ We publish our official binaries on [CloudSmith](https://cloudsmith.com) rather than Maven Central, because CloudSmith
is awesome.

If you want to call this utility from your own Java application, add these sections to `pom.xml` to install:

```xml
<dependency>
    <groupId>io.resurface</groupId>
    <artifactId>resurfaceio-importer</artifactId>
    <version>3.5.2</version>
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
<small>&copy; 2016-2023 <a href="https://resurface.io">Resurface Labs Inc.</a></small>
