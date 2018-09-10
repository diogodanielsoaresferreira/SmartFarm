FROM jenkins/jenkins:lts
USER root
RUN apt-get update && \
    apt-get install -y maven && \
    curl -L https://github.com/docker/compose/releases/download/1.18.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose
