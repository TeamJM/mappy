FROM openjdk:13-alpine

ENV TINI_VERSION v0.18.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

RUN mkdir /app
COPY build/libs/Mappy.jar /app

WORKDIR /app
ENTRYPOINT ["/tini", "--"]

ENV COLOUR_JOURNEYMAP_GREEN=169718
ENV NO_CONFIG=1

CMD ["java", "-jar", "Mappy.jar"]
