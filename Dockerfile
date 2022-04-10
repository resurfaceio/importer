FROM maven:3.8.4-jdk-8-slim
RUN mkdir -p /app
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN ["mvn", "package"]

FROM resurfaceio/alpine-jdk11:3.15.0-x
COPY --from=0 /app/target/*.jar ./
ENTRYPOINT echo "waiting for ${START_DELAY:-10} secs"; sleep ${START_DELAY:-10}; wget $FILE_URL && java -DFILE=./coinbroker.ndjson.gz -DREPEAT=yes -DSATURATED_STOP=${SATURATED_STOP:-no} -Xmx256M -jar main-jar-with-dependencies.jar