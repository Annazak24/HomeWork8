FROM jenkins/jenkins:jdk21

USER root

RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    pip3 install --no-cache-dir jenkins-job-builder jinja2 --break-system-packages && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

USER jenkins