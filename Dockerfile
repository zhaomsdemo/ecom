FROM openjdk:11
ARG JAR_FILE=build/libs/ecom-0.0.2-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} ecom.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","ecom.jar"]