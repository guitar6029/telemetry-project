# maven image
FROM maven:3.9-eclipse-temurin-21

# workdir src dir
WORKDIR /src

# Copy maven wrapper and project definition first
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# give permission to execute
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# copy src 
COPY src src

CMD ["./mvnw", "spring-boot:run"]