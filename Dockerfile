FROM centos:7
LABEL authors="Eric Wittmann <eric.wittmann@redhat.com>"
ENV RELEASE_PATH target/ocp-rest-api-swarm.jar
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

ENV DB_DRIVER_NAME h2
ENV DB_CONNECTION_URL=jdbc:h2:mem:test
ENV DB_USER_NAME=sa
ENV DB_PASSWORD=sa

CMD java -jar /opt/ocp-rest-api/ocp-rest-api-swarm.jar \
    -Dswarm.datasources.data-sources.OcpDS.driver-name=${DB_DRIVER_NAME} \
    -Dswarm.datasources.data-sources.OcpDS.connection-url=${DB_CONNECTION_URL} \
    -Dswarm.datasources.data-sources.OcpDS.user-name=${DB_USER_NAME} \
    -Dswarm.datasources.data-sources.OcpDS.password=${DB_PASSWORD}
