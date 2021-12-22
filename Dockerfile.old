FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} back-end.jar
RUN bash -c 'touch /back-end.jar'
ENTRYPOINT ["java","-Djava.security.egd=:/dev/./urandom/","-jar","/back-end.jar"]