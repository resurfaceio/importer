#!/bin/bash

mvn clean package
mv target/main-jar-with-dependencies.jar target/resurface-importer.jar
cloudsmith push raw resurfaceio/release target/resurface-importer.jar --version $1
