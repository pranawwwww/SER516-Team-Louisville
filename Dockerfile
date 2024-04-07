# Use the official OpenJDK 17 image based on Debian 11 (Bullseye) as the base image
FROM openjdk:17-bullseye

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Install JavaFX dependencies
RUN apt-get update && apt-get install -y libavcodec-extra openjfx && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

#CMD ["sh"]

ENV DISPLAY=:0

# Set the entry point to run the JAR file
#CMD ["java", "--module-path", "/usr/share/openjfx/lib", "--add-modules=javafx.controls,javafx.fxml", "-Dprism.order=sw", "-Dprism.text=t2k", "-Dprism.verbose=true", "-Dheadless=true", "-jar", "target/Louisville-1.0-SNAPSHOT.jar"]
CMD ["java", "-jar", "target/Louisville-1.0-SNAPSHOT.jar"]
