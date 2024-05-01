FROM openjdk:21-jdk

WORKDIR /app

COPY target/jobpostapi-*.jar /app/jobpostapi.jar

EXPOSE 8080

CMD ["java", "-jar", "jobpostapi.jar"]