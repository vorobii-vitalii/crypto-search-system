FROM openjdk:21-jdk

MAINTAINER Vitalii Vorobii

ADD build/libs/crypto-indexer-all.jar ./app.jar

ENTRYPOINT java -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9011 \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Dcom.sun.management.jmxremote.local.only=false \
  -XX:+UseZGC -XX:+ZGenerational \
  -agentlib:jdwp=transport=dt_socket,address=*:8006,server=y,suspend=n -jar app.jar