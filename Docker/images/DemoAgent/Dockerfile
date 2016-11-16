FROM centos:7.2.1511
MAINTAINER Jeremy Dyer <jdye64@gmail.com>

# Add the MiNiFi binaries
ADD ./minifi-0.0.1-bin.zip /

RUN yum update -y

RUN yum install -y vim tar git
RUN unzip minifi-0.0.1-bin.zip