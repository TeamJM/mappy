FROM openjdk:13-alpine

RUN mkdir /app
COPY build/libs/Mappy-*-SNAPSHOT.jar /app
RUN mv /app/Mappy-*-SNAPSHOT.jar /app/Mappy.jar

WORKDIR /app

CMD ["java", "-jar", "/Mappy.jar"]
