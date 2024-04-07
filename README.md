# SER516-Team-Louisville

## Taiga API Integration

This project is a Java application for interacting with the Taiga API to perform various task and calculating metrics.


## Setting up the application

### 1) Clone the repository


   ```bash
   git clone https://github.com/ser516asu/SER516-Team-Louisville.git
   cd SER516-Team-Louisville
   ```

### 2) Compile and Run the application

Go to the project root and compile the Maven project

```bash
   mvn compile
   ```

Now, run the project using following command

```bash
   mvn compile exec:java -Dexec.mainClass=Main
   ```
### 3) To Test

```bash
   mvn test
   ```

### 4) To create package and run the jar executable

```bash
   mvn clean package
   java -jar target/Louisville-release.jar
   ```
### 5) Build and Run using docker

We will open two terminals.

```bash
   #TERMINAL-1
   docker build -t <image-name> .
   
   #MOVE TO TERMINAL-2
   socat TCP-LISTEN:6000,reuseaddr,fork UNIX-CLIENT:\"$DISPLAY\"
   
   #MOVE TO TERMINAL-1
   docker run -v /tmp/.X11-unix:/tmp/.X11-unix -e DISPLAY=$(ipconfig getifaddr en0):0 <image-name>
   ```

### NOTE

Steps to install socat

In case you don't have Maven installed, please refer to following tutorial

https://phoenixnap.com/kb/install-maven-windows


