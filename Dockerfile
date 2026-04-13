# Stage 1: Build the WAR file using Maven
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app

# Copy pom.xml first to leverage Docker caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run with Tomcat
FROM tomcat:9.0-jdk11

# Remove default ROOT webapp
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy built WAR file as ROOT.war
COPY --from=build /app/target/pothole-detection.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
