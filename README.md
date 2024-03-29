# resurfaceio-importer
Import NDJSON into Resurface database

This open-source Java utility imports API calls (stored in [NDJSON format](https://resurface.io/json.html)) to
a remote Resurface database. This command-line utility works with Resurface databases on Kubernetes or Docker,
and includes a few helpful options for load testing.

[![CodeFactor](https://www.codefactor.io/repository/github/resurfaceio/importer/badge)](https://www.codefactor.io/repository/github/resurfaceio/importer)
[![License](https://img.shields.io/github/license/resurfaceio/importer)](https://github.com/resurfaceio/importer/blob/v3.6.x/LICENSE)
[![Contributing](https://img.shields.io/badge/contributions-welcome-green.svg)](https://github.com/resurfaceio/importer/blob/v3.6.x/CONTRIBUTING.md)

## Usage

Download executable jar:
```
wget https://dl.cloudsmith.io/public/resurfaceio/public/maven/io/resurface/resurfaceio-importer/3.6.1/resurfaceio-importer-3.6.1.jar
```

Importing a local file:
```
java -DFILE=XXX.ndjson.gz -DHOST=localhost -DPORT=443 -DBATCH_SIZE=128 -DLIMIT_MESSAGES=0 -DLIMIT_MILLIS=0 -DREPEAT=no -DSATURATED_STOP=no -Xmx512M -jar resurfaceio-importer-3.6.1.jar
```

⚠️ This utility reads files in .ndjson.gz format exclusively. This compressed file format can be exported from a
Resurface database, or generated using the [ndjson](https://github.com/resurfaceio/ndjson) library.

## Parameters

```
FILE: local .ndjson.gz file to import to remote database
HOST: machine name for remote database
PORT: network port for remote database (80 or 443 for Kubernetes, 7701 for Docker)

BATCH_SIZE: default is '128', messages sent in a single POST
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
    <version>3.6.1</version>
</dependency>
```

```xml
<repositories>
    <repository>
        <id>resurfaceio-public</id>
        <url>https://dl.cloudsmith.io/public/resurfaceio/public/maven/</url>
        <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </releases>
    </repository>
</repositories>
```

---
<small>&copy; 2016-2024 <a href="https://resurface.io">Graylog, Inc.</a></small>
