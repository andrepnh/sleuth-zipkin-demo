from gradle:5.2.1-jdk11-slim as builder

COPY --chown=gradle:gradle build.gradle settings.gradle /home/gradle/app/
COPY --chown=gradle:gradle ./src /home/gradle/app/src
WORKDIR /home/gradle/app/
RUN gradle build

FROM gcr.io/distroless/java:11
COPY --from=builder /home/gradle/app/build/libs/inventory-reservation-0.0.1-SNAPSHOT.jar /app/inventory-reservation-0.0.1-SNAPSHOT.jar
WORKDIR /app
CMD ["inventory-reservation-0.0.1-SNAPSHOT.jar"]