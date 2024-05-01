# Job Post API
The Job Post API is a Spring Boot application designed to provide an interface for managing job postings/advertisements.

Used with `https://github.com/JonathanD01/job-post-frontend`

## Requirements
* JDK 21
* Maven
* Docker (for containerization)


## Getting started

1. Clone the repository
```
git clone https://github.com/JonathanD01/job-post-api.git
```

2. Build the project
```
cd job-post-api
mvn clean package
```

3. Run the Application
```
java -jar target/jobpostapi-0.0.1-SNAPSHOT.jar
```

4. Access the API
Once the application is running, you can access the API at http://localhost:8081/api/v1/jobposts.

## Configuration
* **Database configuration**: Configure database connection in properties in the `application.properties` file.