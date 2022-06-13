FROM seberino/maven:latest
LABEL io.openshift.s2i.scripts-url="image:///usr/libexec/s2i" \
      io.openshift.expose-services="8080:http"

ENV JAR_FILE /app.jar
ENV SETUP_FILE /tmp/src/Gm4cArtefatos/Docker/setup_image

COPY assemble /usr/libexec/s2i/
COPY run /usr/libexec/s2i/
COPY usage /usr/libexec/s2i/
COPY save-artifacts /usr/libexec/s2i/


EXPOSE 8080

USER 1001

CMD [ "/usr/libexec/s2i/usage" ]
