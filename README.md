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
sdk install java 25.0.3-amzn
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
- Spring Boot: 4.1.0
- Project metadata:
  - Group: org.sdato
  - Artifact: geocell
  - Package name: org.sdato.geocell
  - Name: geocell
  - Packaging: .jar
  - Configuration: Properties
  - Java: 25
- Dependencies:
  - Spring Web
  - Spring Data JPA
  - PostgreSQL Driver
  - Spring Security
  - Spring Boot DevTools
 
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
> ./gradlew clean build -x test

BUILD SUCCESSFUL in 2s
6 actionable tasks: 6 executed

```

In ```src/test/kotlin/sdato.geocell/GeocellApplicationTests``` run the **GeoCellApplicationTests**

```sh
/home/oicnanev/.jdks/ms-25.0.3/bin/java -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dmanagement.endpoints.jmx.exposure.include=* -javaagent:/home/oicnanev/.cache/JetBrains/RemoteDev/dist/8bf3a2a2b237b_idea-262.8117.19/lib/idea_rt.jar=39191 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /home/oicnanev/GeoCell/geocell/build/classes/kotlin/main:/home/oicnanev/GeoCell/geocell/build/resources/main:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-webmvc/4.1.0/bbde05100fc621297f7dcb5246f631e5af390ef7/spring-boot-starter-webmvc-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/tools.jackson.module/jackson-module-kotlin/3.1.4/ecd703f692239d1150715ea8a481a71349c2ad0f/jackson-module-kotlin-3.1.4.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-reflect/2.3.21/6acc55c52691ce3fd41b20a0472f3513106521a5/kotlin-reflect-2.3.21.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/2.3.21/a2ee2c4e220fee522f0451b723bab2a43f3481a7/kotlin-stdlib-2.3.21.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-data-jpa/4.1.0/252e31911d5042a8ae811ad9bfc3e18ec4ac42a5/spring-boot-starter-data-jpa-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-security/4.1.0/6993d3e5d7bcc0fb4e8e6aab775bc25b197ef814/spring-boot-starter-security-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-jackson/4.1.0/2feada57005e69f8d939815af7400b43f2c07b9e/spring-boot-starter-jackson-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-tomcat/4.1.0/6dbb6ad6bb33b7f1ce15a5822cfd1443966245e0/spring-boot-starter-tomcat-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter/4.1.0/4d1f5fcf982ec75223a3ee44169e268b6dd044f7/spring-boot-starter-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-webmvc/4.1.0/2cb542ef7d4fa572d3031616544db20b6f7de4c7/spring-boot-webmvc-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-http-converter/4.1.0/c3b90921e920dfa7dcb78a5045f18ca0e4bc0b44/spring-boot-http-converter-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/tools.jackson.core/jackson-databind/3.1.4/c7be3c757289893a791b2a4967ec09d1a8b6c576/jackson-databind-3.1.4.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.21/b1bc1868bf02dc0bd6c7836257a036a331005309/jackson-annotations-2.21.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.jetbrains/annotations/13.0/919f0dfe192fb4e063e7dacadee7f8bb9a2672a9/annotations-13.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-jdbc/4.1.0/aa7c6585dd28891cb052d95606776076b5445df2/spring-boot-starter-jdbc-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-data-jpa/4.1.0/d29dbdc6734afe2f4d5c56cf3112c491c3582642/spring-boot-data-jpa-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-jdbc/4.1.0/71f00c6af0ae910530d9fce8f5ae6e54c595f690/spring-boot-jdbc-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-security/4.1.0/1fe5f10c43305d43d8ac22ae5b721010f4394449/spring-boot-security-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-aop/7.0.8/b73e58e9b7dbc4db6b4ebbe3336be0e901b0d261/spring-aop-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-jackson/4.1.0/bada14f54b1961279f9942c54b5039c90cd6a252/spring-boot-jackson-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-tomcat-runtime/4.1.0/6107a851e178c1233b64634270b11b8d5490733a/spring-boot-starter-tomcat-runtime-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-tomcat/4.1.0/502aeed70cb11c3754c365324acd9f3ee746354/spring-boot-tomcat-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-autoconfigure/4.1.0/a921df4e39389e43dacac5615a1185708ee8a960/spring-boot-autoconfigure-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-starter-logging/4.1.0/f2e2615d9fb27a035a8fc7aef19670769739ae76/spring-boot-starter-logging-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/jakarta.annotation/jakarta.annotation-api/3.0.0/54f928fadec906a99d558536756d171917b9d936/jakarta.annotation-api-3.0.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.yaml/snakeyaml/2.6/2bc14918a2f8d5414749ab12d0c590cd3198b8c1/snakeyaml-2.6.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-webmvc/7.0.8/d08172cf66f9625a42935d596ca4c83d21265996/spring-webmvc-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-servlet/4.1.0/a067e9b4b7c255a8587852b6eb842413cc458e70/spring-boot-servlet-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-web/7.0.8/d7cddac31ee023d25d37f48e2ff0e168cf635c14/spring-web-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot/4.1.0/bcf26d4c27ab080ef60c13220e96a37d687f9e26/spring-boot-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/tools.jackson.core/jackson-core/3.1.4/f9fd39204bd3d4922b42f6a54b81f46bd8ed575a/jackson-core-3.1.4.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/com.zaxxer/HikariCP/7.0.2/c2b43c946b86a14a96342379e22b004c56c6166d/HikariCP-7.0.2.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-hibernate/4.1.0/af702315125871004a8a7df290f55ac5e5617d91/spring-boot-hibernate-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.data/spring-data-jpa/4.1.0/6387c88a4a9637f129e0ebcad50ada478ad01c3f/spring-data-jpa-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-data-commons/4.1.0/3d587cffcde8c18fa784e0486a2c0a8a4728f262/spring-boot-data-commons-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-aspects/7.0.8/daf535194ee6cd1e1e23c7368c58b5f02fe18f65/spring-aspects-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-sql/4.1.0/7a5ef86020d10e926b2e98e1d7b4b79258fb2f38/spring-boot-sql-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-transaction/4.1.0/93b102fb7c2eb6558b8e52fb24f1152c837db3f6/spring-boot-transaction-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-jdbc/7.0.8/f979da380cd01fd363510ca98a7f544d1959a914/spring-jdbc-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-config/7.1.0/ebc863ad43703c6f5517881e16d3d73de80cd193/spring-security-config-7.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-web/7.1.0/cec1635393d11a56da5ff111253c88056d5cc0eb/spring-security-web-7.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-beans/7.0.8/829cdf4c82a9409e0e10922b9e57e2ef12d5f04d/spring-beans-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-core/7.0.8/25f5ebd179f92d7d12779e3761c96da4b0109ad/spring-core-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-web-server/4.1.0/e67513b382c2cef7b8bc364e8e85cd7f0940e331/spring-boot-web-server-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.apache.tomcat.embed/tomcat-embed-websocket/11.0.22/5686ff1ad745491d981d771514027d8fc55aed4f/tomcat-embed-websocket-11.0.22.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.apache.tomcat.embed/tomcat-embed-core/11.0.22/cc7307efaa60c0d31784bf053ec4aa48a404f44b/tomcat-embed-core-11.0.22.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.apache.tomcat.embed/tomcat-embed-el/11.0.22/db92447e16c3561e9ca7bf9311b6b6389816699e/tomcat-embed-el-11.0.22.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/ch.qos.logback/logback-classic/1.5.34/e598899bad5824511cd3019299254230468e1fa2/logback-classic-1.5.34.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-to-slf4j/2.25.4/68df56640a5d245192e91bd2ac89e504b477cc10/log4j-to-slf4j-2.25.4.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.slf4j/jul-to-slf4j/2.0.18/79739c98001d5c9d078d087d5a348ec9e474ec8f/jul-to-slf4j-2.0.18.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-context/7.0.8/6c58b590fd8d71afc36797cf6555e82500f1eb64/spring-context-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-expression/7.0.8/bb844eefd513820e356b575d7ea0f7084147af59/spring-expression-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/io.micrometer/micrometer-observation/1.17.0/2037bdaeb5725bdddb2390cbf93be3c19f8b8b48/micrometer-observation-1.17.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/2.0.18/78a9e7a37cd6360e0b818e86341b24123d28d4df/slf4j-api-2.0.18.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-jpa/4.1.0/bf645cdf194c71f829225793a5d6545d9dfe96ae/spring-boot-jpa-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-orm/7.0.8/8c40fad8ecc72337353818da54ef2d104b3fa7a9/spring-orm-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.hibernate.orm/hibernate-core/7.4.1.Final/b405096e4aa04ff9c99a3e6e632eb2b8d252e1f1/hibernate-core-7.4.1.Final.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.data/spring-data-commons/4.1.0/e8d1077d09c20e9ecf3574a72ce5cc90a29b902a/spring-data-commons-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework/spring-tx/7.0.8/96c66acfb3920457421a9cb3648a32e62f42f815/spring-tx-7.0.8.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.antlr/antlr4-runtime/4.13.2/fc3db6d844df652a3d5db31c87fa12757f13691d/antlr4-runtime-4.13.2.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-persistence/4.1.0/12b6d5aa8a97c458a5c8eba8e8cb0e38e326d9d8/spring-boot-persistence-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.aspectj/aspectjweaver/1.9.25.1/a713c790da4d794c7dfb542b550d4e44898d5e23/aspectjweaver-1.9.25.1.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-core/7.1.0/50064b46aed22d05aa181b2c95d47989c7c99160/spring-security-core-7.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/commons-logging/commons-logging/1.3.6/63e78ca6cd446c0ad166d14f03ed99e7efb3896d/commons-logging-1.3.6.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.jspecify/jspecify/1.0.0/7425a601c1c7ec76645a78d22b8c6a627edee507/jspecify-1.0.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/ch.qos.logback/logback-core/1.5.34/378692f76c337b3325c15bffb89e013dc1f897b4/logback-core-1.5.34.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-api/2.25.4/89ff2217b193fb187b134aa6ebcbfa8a28b018a9/log4j-api-2.25.4.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/biz.aQute.bnd/biz.aQute.bnd.annotation/7.1.0/2f2be18c936d08cf46ea6cfa0043f34afdf38705/biz.aQute.bnd.annotation-7.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/com.google.errorprone/error_prone_annotations/2.38.0/fc0ae991433e8590ba51cd558421478318a74c8c/error_prone_annotations-2.38.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.osgi/org.osgi.annotation.bundle/2.0.0/f5c2cd6e670c3c5d440d3003efd5ef2ead5c68eb/org.osgi.annotation.bundle-2.0.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.osgi/org.osgi.annotation.versioning/1.1.2/dc3cd4ec96c0b3c5459fe00694bd73a816ecf93e/org.osgi.annotation.versioning-1.1.2.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/io.micrometer/micrometer-commons/1.17.0/2edbb561cdd2b2777fbcd83aa9f2204ebabf592b/micrometer-commons-1.17.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/jakarta.persistence/jakarta.persistence-api/3.2.0/bb75a113f3fa191c2c7ee7b206d8e674251b3129/jakarta.persistence-api-3.2.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/jakarta.transaction/jakarta.transaction-api/2.0.1/51a520e3fae406abb84e2e1148e6746ce3f80a1a/jakarta.transaction-api-2.0.1.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-crypto/7.1.0/819461a33c5b10b92bb1a125db933a17fc0db792/spring-security-crypto-7.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.osgi/org.osgi.resource/1.0.0/343c88949132984b3f2d4175a72c40b77dc65619/org.osgi.resource-1.0.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.osgi/org.osgi.service.serviceloader/1.0.0/3728ff37909c6a3c1870b3e851342d9f2bd3cc63/org.osgi.service.serviceloader-1.0.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.springframework.boot/spring-boot-devtools/4.1.0/b1d5779b74060006b173e3d2b8b3e93d8768a95e/spring-boot-devtools-4.1.0.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.postgresql/postgresql/42.7.11/4c21cdd1b3938f400703716d37c4e8ca4d332808/postgresql-42.7.11.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.hibernate.models/hibernate-models/1.1.1/c890a4cc781fd8720e0e2613b4a24b33cdaa80a1/hibernate-models-1.1.1.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.jboss.logging/jboss-logging/3.6.3.Final/1cc9f976725720bb4a66f80af3e3aa6b9890d969/jboss-logging-3.6.3.Final.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/net.bytebuddy/byte-buddy/1.18.10/5b34812ced047973a6d42654d50c3b69124ce587/byte-buddy-1.18.10.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/jaxb-runtime/4.0.9/500c199572538675d2819c938fbafe34935d8d6b/jaxb-runtime-4.0.9.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/jakarta.xml.bind/jakarta.xml.bind-api/4.0.5/161811f36cad3c65991502e80317f2f6703361df/jakarta.xml.bind-api-4.0.5.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/jakarta.inject/jakarta.inject-api/2.0.1/4c28afe1991a941d7702fe1362c365f0a8641d1e/jakarta.inject-api-2.0.1.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/jaxb-core/4.0.9/3f32ec949d109d666fa30994223d1c25a47b105f/jaxb-core-4.0.9.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/jakarta.activation/jakarta.activation-api/2.1.4/9e5c2a0d75dde71a0bedc4dbdbe47b78a5dc50f8/jakarta.activation-api-2.1.4.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.eclipse.angus/angus-activation/2.0.3/7f80607ea5014fef0b1779e6c33d63a88a45a563/angus-activation-2.0.3.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/org.glassfish.jaxb/txw2/4.0.9/70325ebdebca3e7aae1dfa395fd41fc25d8a6f4e/txw2-4.0.9.jar:/home/oicnanev/.gradle/caches/modules-2/files-2.1/com.sun.istack/istack-commons-runtime/4.1.2/18ec117c85f3ba0ac65409136afa8e42bc74e739/istack-commons-runtime-4.1.2.jar org.sdato.geocell.GeocellApplicationKt

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.0)

2026-06-23T14:14:37.408Z  INFO 34610 --- [geocell] [  restartedMain] org.sdato.geocell.GeocellApplicationKt   : Starting GeocellApplicationKt using Java 25.0.3 with PID 34610 (/home/oicnanev/GeoCell/geocell/build/classes/kotlin/main started by oicnanev in /home/oicnanev/GeoCell)
2026-06-23T14:14:37.411Z  INFO 34610 --- [geocell] [  restartedMain] org.sdato.geocell.GeocellApplicationKt   : No active profile set, falling back to 1 default profile: "default"
2026-06-23T14:14:37.454Z  INFO 34610 --- [geocell] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2026-06-23T14:14:37.454Z  INFO 34610 --- [geocell] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2026-06-23T14:14:37.874Z  INFO 34610 --- [geocell] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-06-23T14:14:37.890Z  INFO 34610 --- [geocell] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 7 ms. Found 0 JPA repository interfaces.
2026-06-23T14:14:38.189Z  INFO 34610 --- [geocell] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 8080 (http)
2026-06-23T14:14:38.199Z  INFO 34610 --- [geocell] [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-06-23T14:14:38.199Z  INFO 34610 --- [geocell] [  restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.22]
2026-06-23T14:14:38.217Z  INFO 34610 --- [geocell] [  restartedMain] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 763 ms
2026-06-23T14:14:38.327Z  INFO 34610 --- [geocell] [  restartedMain] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]
2026-06-23T14:14:38.360Z  INFO 34610 --- [geocell] [  restartedMain] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.4.1.Final
2026-06-23T14:14:38.657Z  INFO 34610 --- [geocell] [  restartedMain] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-06-23T14:14:38.677Z  INFO 34610 --- [geocell] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-06-23T14:14:38.904Z  INFO 34610 --- [geocell] [  restartedMain] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@527e66d1
2026-06-23T14:14:38.905Z  INFO 34610 --- [geocell] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-06-23T14:14:38.923Z  WARN 34610 --- [geocell] [  restartedMain] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-06-23T14:14:38.937Z  INFO 34610 --- [geocell] [  restartedMain] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
	Database JDBC URL [jdbc:postgresql://localhost:5432/geocell]
	Database driver: PostgreSQL JDBC Driver
	Database dialect: PostgreSQLDialect
	Database version: 18.4
	Default catalog/schema: geocell/public
	Autocommit mode: undefined/unknown
	Isolation level: READ_COMMITTED [default READ_COMMITTED]
	JDBC fetch size: none
	Pool: DataSourceConnectionProvider
	Minimum pool size: undefined/unknown
	Maximum pool size: undefined/unknown
2026-06-23T14:14:39.206Z  INFO 34610 --- [geocell] [  restartedMain] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-06-23T14:14:39.242Z  INFO 34610 --- [geocell] [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-06-23T14:14:39.666Z  WARN 34610 --- [geocell] [  restartedMain] .s.a.UserDetailsServiceAutoConfiguration : 

Using generated security password: cc036ae6-df3f-40bd-8d73-25ff3c13f577

This generated password is for development use only. Your security configuration must be updated before running your application in production.

2026-06-23T14:14:39.692Z  INFO 34610 --- [geocell] [  restartedMain] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with UserDetailsService bean with name inMemoryUserDetailsManager
2026-06-23T14:14:39.810Z  INFO 34610 --- [geocell] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
2026-06-23T14:14:39.815Z  INFO 34610 --- [geocell] [  restartedMain] org.sdato.geocell.GeocellApplicationKt   : Started GeocellApplicationKt in 2.668 seconds (process running for 3.101)
2026-06-23T14:16:48.138Z  INFO 34610 --- [geocell] [   File Watcher] rtingClassPathChangeChangedEventListener : Restarting due to 1 class path change (0 additions, 0 deletions, 1 modification)
2026-06-23T14:16:48.141Z  INFO 34610 --- [geocell] [       Thread-1] o.s.boot.tomcat.GracefulShutdown         : Commencing graceful shutdown. Waiting for active requests to complete
2026-06-23T14:16:48.142Z  INFO 34610 --- [geocell] [tomcat-shutdown] o.s.boot.tomcat.GracefulShutdown         : Graceful shutdown complete
2026-06-23T14:16:48.146Z  INFO 34610 --- [geocell] [       Thread-1] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2026-06-23T14:16:48.148Z  INFO 34610 --- [geocell] [       Thread-1] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2026-06-23T14:16:48.156Z  INFO 34610 --- [geocell] [       Thread-1] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.0)

2026-06-23T14:16:48.211Z  INFO 34610 --- [geocell] [  restartedMain] org.sdato.geocell.GeocellApplicationKt   : Starting GeocellApplicationKt using Java 25.0.3 with PID 34610 (/home/oicnanev/GeoCell/geocell/build/classes/kotlin/main started by oicnanev in /home/oicnanev/GeoCell)
2026-06-23T14:16:48.211Z  INFO 34610 --- [geocell] [  restartedMain] org.sdato.geocell.GeocellApplicationKt   : No active profile set, falling back to 1 default profile: "default"
2026-06-23T14:16:48.299Z  INFO 34610 --- [geocell] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-06-23T14:16:48.301Z  INFO 34610 --- [geocell] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 0 ms. Found 0 JPA repository interfaces.
2026-06-23T14:16:48.350Z  INFO 34610 --- [geocell] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 8080 (http)
2026-06-23T14:16:48.351Z  INFO 34610 --- [geocell] [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-06-23T14:16:48.351Z  INFO 34610 --- [geocell] [  restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.22]
2026-06-23T14:16:48.360Z  INFO 34610 --- [geocell] [  restartedMain] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 147 ms
2026-06-23T14:16:48.385Z  INFO 34610 --- [geocell] [  restartedMain] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]
2026-06-23T14:16:48.399Z  INFO 34610 --- [geocell] [  restartedMain] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-06-23T14:16:48.400Z  INFO 34610 --- [geocell] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Starting...
2026-06-23T14:16:48.421Z  INFO 34610 --- [geocell] [  restartedMain] com.zaxxer.hikari.pool.HikariPool        : HikariPool-2 - Added connection org.postgresql.jdbc.PgConnection@7c319ed1
2026-06-23T14:16:48.422Z  INFO 34610 --- [geocell] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Start completed.
2026-06-23T14:16:48.424Z  WARN 34610 --- [geocell] [  restartedMain] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-06-23T14:16:48.427Z  INFO 34610 --- [geocell] [  restartedMain] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
	Database JDBC URL [jdbc:postgresql://localhost:5432/geocell]
	Database driver: PostgreSQL JDBC Driver
	Database dialect: PostgreSQLDialect
	Database version: 18.4
	Default catalog/schema: geocell/public
	Autocommit mode: undefined/unknown
	Isolation level: READ_COMMITTED [default READ_COMMITTED]
	JDBC fetch size: none
	Pool: DataSourceConnectionProvider
	Minimum pool size: undefined/unknown
	Maximum pool size: undefined/unknown
2026-06-23T14:16:48.445Z  INFO 34610 --- [geocell] [  restartedMain] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-06-23T14:16:48.454Z  INFO 34610 --- [geocell] [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-06-23T14:16:48.541Z  WARN 34610 --- [geocell] [  restartedMain] .s.a.UserDetailsServiceAutoConfiguration : 

Using generated security password: 42d8863c-aee8-459a-be22-d8a681a3c0e7

This generated password is for development use only. Your security configuration must be updated before running your application in production.

2026-06-23T14:16:48.552Z  INFO 34610 --- [geocell] [  restartedMain] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with UserDetailsService bean with name inMemoryUserDetailsManager
2026-06-23T14:16:48.607Z  INFO 34610 --- [geocell] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
2026-06-23T14:16:48.610Z  INFO 34610 --- [geocell] [  restartedMain] org.sdato.geocell.GeocellApplicationKt   : Started GeocellApplicationKt in 0.412 seconds (process running for 131.897)
2026-06-23T14:16:48.612Z  INFO 34610 --- [geocell] [  restartedMain] .ConditionEvaluationDeltaLoggingListener : Condition evaluation unchanged
2026-06-23T14:16:49.174Z  INFO 34610 --- [geocell] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-06-23T14:16:49.174Z  INFO 34610 --- [geocell] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-06-23T14:16:49.175Z  INFO 34610 --- [geocell] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
```

## PostgreSQL Server

### Installation

```sh
sudo apt install postgresql-18 postgresql-18-postgis-3
```

### Criar DB e DB_user

```sh
sudo -i -u postgres psql
```

```sh
CREATE USER geocell_user WITH PASSWORD '5up3r_5tr0ng_p455w0rd!';
CREATE DATABASE geocell OWNER geocell_user;
\c geocell
CREATE EXTENSION postgis;
GRANT ALL PRIVILEGES ON DATABASE geocell TO geocell_user;
\q
exit
```
