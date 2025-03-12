#	Defines how to build the image for your application (what base image to use, what files to copy in, what command to run).

# Use OpenJDK 17 (Alpine version for a smaller image size) as the base image
FROM openjdk:17-jdk-alpine

# Copy the built application JAR file from the target folder into the container
COPY target/usermgmtsystem-0.0.1-SNAPSHOT.jar app-1.0.0.jar

# Set the command to run the application when the container starts
ENTRYPOINT [ "java", "-jar", "app-1.0.0.jar" ]