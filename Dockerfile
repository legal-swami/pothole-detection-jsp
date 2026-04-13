# Stage 1: Build the WAR file using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package

# Stage 2: Run the WAR file in Tomcat
FROM tomcat:10.1-jdk17

# Install OpenCV
RUN apt-get update && apt-get install -y libopencv-java && rm -rf /var/lib/apt/lists/*
ENV OPENCV_JAVA_BIN=/usr/share/java/opencv4/opencv-480.jar

# Copy WAR file from build stage
COPY --from=build /app/target/pothole-detection.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
