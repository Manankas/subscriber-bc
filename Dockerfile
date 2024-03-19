FROM eclipse-temurin:17-jre-alpine

WORKDIR /subscriber

COPY ["./target/subscriber-bc-0.0.1-SNAPSHOT.jar", "./subscriber-bc-0.0.1-SNAPSHOT.jar"]

ENTRYPOINT java -jar "./subscriber-bc-0.0.1-SNAPSHOT.jar"