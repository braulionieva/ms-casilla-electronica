FROM openjdk:17-alpine


ENV TZ=America/Lima
RUN apk add --no-cache freetype fontconfig ttf-dejavu tzdata

ADD /build/libs/cfms-casilla-electronica-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
