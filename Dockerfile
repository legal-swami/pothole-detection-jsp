FROM tomcat:10.1-jdk17

# OpenCV Library Install करा
RUN apt-get update && apt-get install -y libopencv-java
ENV OPENCV_JAVA_BIN=/usr/share/java/opencv4/opencv-480.jar

# आपला WAR File Tomcat मध्ये Copy करा
COPY target/pothole-detection.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
