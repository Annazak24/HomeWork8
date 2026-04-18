FROM centos:centos8.4.2105

# FIX broken repos (CentOS 8 EOL)
RUN sed -i 's|^mirrorlist=|#mirrorlist=|g' /etc/yum.repos.d/CentOS-* && \
    sed -i 's|^#baseurl=http://mirror.centos.org|baseurl=https://vault.centos.org|g' /etc/yum.repos.d/CentOS-* && \
    yum clean all && \
    yum makecache

# Install Python + pip + jenkins-job-builder
RUN yum update -y && \
    yum install -y python3 python3-pip && \
    pip3 install --no-cache-dir jinja2 'jenkins-job-builder>=3.11.0'

# Create working dir
RUN mkdir -p /root/jenkins-deployer/config/jobs/jenkins-job-builder
WORKDIR /root/jenkins-deployer/config/jobs/jenkins-job-builder

# Copy project
COPY . .

CMD ["bash"]