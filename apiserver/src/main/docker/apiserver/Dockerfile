FROM openjdk:8-jdk-alpine

ENV PAYARA_PATH /opt/payara

RUN   apk update \
 &&   apk add ca-certificates wget \
 &&   update-ca-certificates && \
 mkdir -p $PAYARA_PATH

ENV PKG_FILE_NAME apiserver.jar
ENV PAYARA_MICRO_JAR=$PAYARA_PATH/$PKG_FILE_NAME

# Default payara ports to expose
EXPOSE 4848 8009 8080 8181

WORKDIR $PAYARA_PATH

COPY $PKG_FILE_NAME .
ENTRYPOINT ["java", "-jar", "/opt/payara/apiserver.jar"]
