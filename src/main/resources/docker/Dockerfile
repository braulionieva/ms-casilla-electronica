FROM gradle:jdk17-alpine AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon
RUN ls build/libs/

FROM amazoncorretto:17.0.7-alpine3.16
EXPOSE 8080
RUN mkdir /app
COPY --from=builder /home/gradle/src/build/libs/notifications-0.0.1-SNAPSHOT.jar /app/application.jar
ENTRYPOINT ["java", "-jar","/app/application.jar"]
