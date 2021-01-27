# Squid Whitelist API

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.

The goal of this project is to build an API to facilitate the daily administration of a Squid proxy server in whitelist only mode.

Currently this API is comprised of two basic features.  
1. Add or delete domains from the whitelist (and prompt Squid to reload the configuration).
2. Start and stop timed whitelist bypass mode.

# Project File Structure
```
├── scripts
│   ├── disable_bypass.sh                                           Script to disable proxy bypass (copy to your linux host)
│   └── enable_bypass.sh                                            Script to enable proxy bypass (copy to your linux host)
├── src
│   └── main
│       ├── java
│       │   └── us
│       │       └── livebythecode
│       │           ├── rest
│       │           │   ├── model
│       │           │   │   └── ScheduledBypassReset.java           DTO used to return active bypass reset timers
│       │           │   └── services
│       │           │       └── resources
│       │           │           ├── AuthenticateResource.java       Endpoint for retrieving a JWT
│       │           │           ├── ScheduleBypassResource.java     Endpoint for initiating whitelist bypass
│       │           │           └── WhitelistResource.java          Endpoint for whitelist CRUD operations
│       │           └── security
│       │               ├── auth
│       │               │   └── SuperSimpleUserAuthService.java     Stupidly simple authentication service 
│       │               └── jwt
│       │                   └── TokenService.java                   Service to generate and sign JWT
│       └── resources
│           ├── application.properties                              Where all the configuration lives

```

# Running the application in dev mode

Note that the application (by default) expects 2 whitelist files (`/etc/squid/whitelist1.acl` and `/etc/squid/whitelist2`).acl  

You can run the application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```
Note that most of the API is secured by JWT. To retrieve a token that can be used for subsequent calls, first make a request to:
[http://{{hostname}}:{{portnumber}}/squid-configuration/authenticate?user=admin&password=somepass](http://localhost:8080/squid-configuration/authenticate?user=admin&password=somepass)  
Then use the retrieved token as an authorization bearer for subsequent calls.

# Swagger UI

Available at http://localhost:8080/q/swagger-ui/

# Postman 

Example API calls are provided in [Postman](https://www.postman.com/) collection below.

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/f7adb836f4ec7a0d0345)


# Curl

You really like using the command line huh? OK...  
You should be able to retrieve a token using:  
```bash
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST "http://localhost:8080/squid-configuration/authenticate?user=admin&password=somepass"
```
Then paste the token from the command above into the command below to add 1reallygreatdomain.com to the whitelist.

```bash
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImdyb3VwcyI6WyJBZG1pbiJdLCJpYXQiOjE2MTE3NzkzNDMsImV4cCI6MTYxMTc4MTE0M30.nZrRAlvT8wzEJpnMxqFBVbI1M1P7-p4QfKLJO-Xbp_k6si6LFMRKcpippx1ywahBw-zyENUKX1CUWCiQDBNoGYVDkOdw9gVt9S_dqCGHGevaND-AcAnTWVubFjYX0EaIfnafgNz_nuVJQXJX_eMQutXPFb6ddfrxgnB8aAp4Aes13J3k2T07WRFulvMGfsyhdgOfzpg_2t3uL-EuVGWUVO4xpq8qm2iwop943-QR3URyChkP-8qJAKdDGbiAHBScYLrKDN9-Y_Y4xSA781OiNMcoRUofhc9gq6yMwbhfBzyVgqUzL-sBnoPaRjtrtlUFPAS5FDz2j6SsKunzZZcWdQ" "http://localhost:8080/squid-configuration/whitelist-domains/0?domainName=1reallygreatdomain.com"
```

# Generating asymetric keys (using openssl)

(Note that the public and private keys are configured in [application.properties](./src/main/resources/application.properties))

The project includes some default keys, but you may want to generate your own using the following openssl commands.
```bash
openssl genrsa -out rsaPrivateKey.pem 2048
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
```

openssl command for converting the private key to PKCS#8 format (the format that's required by the app):
```bash
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
```



# Packaging and running the application

The application can be packaged using:
```shell script
./mvn package
```
It produces the `squid-configuration-services-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvn package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/squid-configuration-services-1.0.0-SNAPSHOT-runner.jar`.

# Creating a native executable

You can create a native executable using: 
```shell script
./mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/squid-configuration-services-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

# Other notes:
A file will need to be added to /etc/sudoers.d. Name it squid (assuming you will be running the configuration service as the squid user) and insert a single line like:  
```
%squid ALL=NOPASSWD: /usr/bin/systemctl reload squid.service
```
This will allow the squid user to reload the squid service after the configuration has changed.

There are 2 shell scripts for [enabling](./scripts/enable_bypass.sh) and [disabling](./scripts/disable_bypass.sh) whitelist bypass mode. These will need to be copied somewhere on your host and made executable. Additionally [application.properties](./src/main/resources/application.properties) will need to be updated to reflect the script paths.

# UI
[Whitelist Manager](https://github.com/SimpleGeek/whitelist-manager) Is project that's currently in the works to provide a UI over this API.