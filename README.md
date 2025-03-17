# GeoCell

## Cloud Virtual Machine - Oracle Cloud Ifrastructure

- Available Domain: AD-1
- Fault Domain: FD-2
- Region: eu-madrid-1
- Virtual Cloud Network: (chosen one previosly created for different machines)
- Image Detais: 22.04 Ubuntu Minimal aarch64
- Shape: VM Standard.A1.Flex
- OCPU count: 4
- Network Bandwidth: 4 Gbps
- Memory GB: 14
- Local disk: Block storage only
- VM user: ubuntu
- SSH access: key-pair

  ## First steps on the server

   On our machine:
  ```sh
  ssh -i [ssh-key.key] ubuntu@[Public IP addr]
  ```

On the server:

```sh
sudo apt update && sudo apt upgrade -y && sudo apt autoremove -y
sudo apt install zip unzip git
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.6-amzn
sdk install gradle
sdk install kotlin
mkdir GeoCell
  ```

Git configuration:

```sh
git config --global user.name "[username]"
git config --global user.email "[youremail@mail.net]"
git config --global credential.helper store
git clone https://github.com/oicnanev/GeoCell.git
```

Build a skeleton project at [Spring initializr](https://start.spring.io)

- Project: Gradle-kotlin
- Language: kotlin
- Spring Boot: 3.4.3
- Project metadata:
  - Group: sdato
  - Artifact: geocell
  - Name: geocell
  - Packaging: .jar
  - Java: 21
- Dependencies:
  - Spring Web
  - Spring Session
  - PostgreSQL Driver
 
Download .zip file and then in terminal:

```sh
scp -i [ssh-key.key] geocell.zip ubuntu@[Public IP addr]:~/GeoCell
```

In the server:

```sh
cd GeoCell
unzip geocell.zip
rm geocell.zip
```

Configure IntellIJ to use remote development via ssh:

- connect ssh
- create new connection:
  - Host: [Public IP addr]
  - User: ubuntu
  - Auth: key pair
  - File: [Path to ssh-key.key]
 
   After connection, open terminal in IntellIJ:

```sh
./graldew clean build


Welcome to Gradle 8.12.1!

Here are the highlights of this release:
 - Enhanced error and warning reporting with the Problems API
 - File-system watching support on Alpine Linux
 - Build and test Swift 6 libraries and apps

For more details see https://docs.gradle.org/8.12.1/release-notes.html

OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

BUILD SUCCESSFUL in 16s
9 actionable tasks: 8 executed, 1 up-to-date
```

In ```src/test/kotlin/sdato.geocell/GeocellApplicationTests``` run the **GeoCellApplicationTests**

```sh
> Task :checkKotlinGradlePluginConfigurationErrors
> Task :compileKotlin UP-TO-DATE
> Task :compileJava NO-SOURCE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE
> Task :compileTestKotlin UP-TO-DATE
> Task :compileTestJava NO-SOURCE
> Task :processTestResources NO-SOURCE
> Task :testClasses UP-TO-DATE
15:33:37.870 [Test worker] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [sdato.geocell.GeocellApplicationTests]: GeocellApplicationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
15:33:37.984 [Test worker] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration sdato.geocell.GeocellApplication for test class sdato.geocell.GeocellApplicationTests

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.4.3)

2025-02-26T15:33:38.410Z  INFO 16687 --- [geocell] [    Test worker] sdato.geocell.GeocellApplicationTests    : Starting GeocellApplicationTests using Java 21.0.6 with PID 16687 (started by ubuntu in /home/ubuntu/GeoCell/geocell)
2025-02-26T15:33:38.413Z  INFO 16687 --- [geocell] [    Test worker] sdato.geocell.GeocellApplicationTests    : No active profile set, falling back to 1 default profile: "default"
2025-02-26T15:33:39.586Z  INFO 16687 --- [geocell] [    Test worker] sdato.geocell.GeocellApplicationTests    : Started GeocellApplicationTests in 1.449 seconds (process running for 2.617)
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
> Task :test
BUILD SUCCESSFUL in 6s
5 actionable tasks: 2 executed, 3 up-to-date
3:33:40 PM: Execution finished ':test --tests "sdato.geocell.GeocellApplicationTests"'.

```

## PostgreSQL Server

We used an [Alpine Linux](https://alpinelinux.org).

After installation and configuration run:

```sh
doas apk update && doas apk upgrade
doas apk add postgresql postgresql-contrib postgis
```

Start and enable PostgreSQL

```sh
doas rc-service postgresql start
doas rc-update add postgresql 
```

Create the database

```sh
doas su - postgres
createdb geocell
psql geocell
CREATE EXTENSION postgis;
```
