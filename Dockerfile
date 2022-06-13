FROM openjdk:8-jdk-alpine
USER 1000
ENV JAR_FILE /tmp/Gm4cTEF-0.0.1-SNAPSHOT.jar
COPY Gm4cTEF/target/*.jar /tmp
COPY Gm4cSenha/target/*.jar /tmp
COPY Gm4cLimite/target/*.jar /tmp
COPY Gm4cConta/target/*.jar /tmp
COPY Gm4cSpringAdmin/target/*.jar /tmp
COPY run /tmp
ENTRYPOINT ["sh","/tmp/run"]
