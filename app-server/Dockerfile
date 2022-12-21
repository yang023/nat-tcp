FROM openjdk:20-ea-17-jdk
ADD ./nat-app-server.jar /opt/
EXPOSE 8080
EXPOSE 18080
EXPOSE 10243
CMD java ${JAVA_OPTS} -jar /opt/nat-app-server.jar
