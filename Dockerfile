# Stage 1: Build the WAR file
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app

# Copy pom.xml first (to cache dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run with Tomcat
FROM tomcat:9.0-jdk11

# Install OpenCV native library
RUN apt-get update && apt-get install -y libopencv-java && rm -rf /var/lib/apt/lists/*
ENV OPENCV_JAVA_BIN=/usr/share/java/opencv4/opencv-480.jar

# Copy WAR file from build stage
COPY --from=build /app/target/pothole-detection.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
