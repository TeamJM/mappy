FROM openjdk:16-alpine

RUN mkdir /app
COPY build/libs/Mappy-*-all.jar /
RUN mv /Mappy-*-all.jar /Mappy.jar

WORKDIR /app

CMD ["java", "-jar", "/Mappy.jar"]
