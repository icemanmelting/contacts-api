FROM ubuntu:xenial-20171006

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean && \
    update-ca-certificates -f && \
    rm -rf /var/lib/apt/lists/* && \
    rm -rf /var/cache/oracle-jdk8-installer;

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME

WORKDIR /app

ADD ./target/uberjar/contacts-api-standalone.jar /app/contacts-api-standalone.jar

ENTRYPOINT ["java", "-jar", "/app/contacts-api-standalone.jar"]

EXPOSE 8080
