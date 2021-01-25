# squid-configuration-services project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

Note that the application (by default) expects 2 whitelist files /etc/squid/whitelist1.acl and /etc/squid/whitelist2.acl  

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Swagger UI

Available at http://localhost:8080/q/swagger-ui/


## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `squid-configuration-services-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/squid-configuration-services-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/squid-configuration-services-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Other notes:
A file will need to be added to /etc/sudoers.d. Name it squid and insert a single line like:  
```
%squid ALL=NOPASSWD: /usr/bin/systemctl reload squid.service
```
This will allow the squid user to reload the squid service after the configuration has changed.