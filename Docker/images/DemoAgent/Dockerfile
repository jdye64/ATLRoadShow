FROM jdye64/docker-hwx:nifi-1.0.0
MAINTAINER Jeremy Dyer <jdye64@gmail.com>

ADD atlroadshow-webapp-1.0.0.jar /
ADD application.yml /
ADD startupScript.sh /
RUN chmod +x /startupScript.sh

CMD ./startupScript.sh