FROM centos:7
LABEL authors="Eric Wittmann <eric.wittmann@redhat.com>"
ARG RELEASE_PATH
RUN yum install -y \
    java-1.8.0-openjdk-headless \
  && yum clean all

WORKDIR /opt/ocp-rest-api

ADD ${RELEASE_PATH} /opt/ocp-rest-api

RUN groupadd -r ocpra -g 1001 \
    && useradd -u 1001 -r -g ocpra -d /opt/ocp-rest-api/ -s /sbin/nologin -c "Docker image user" ocpra \
    && chown -R ocpra:ocpra /opt/ocp-rest-api/

USER 1001

EXPOSE 8080

CMD ["java", "-jar", "/opt/ocp-rest-api/ocp-rest-api-swarm.jar"]
