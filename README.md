# java8-spring-boot-excel

## Requirements

- [JDK 8](https://adoptium.net/es/temurin/releases)

## Getting started

1. Clone the repository.
2. Download dependencies via `./mvnw install` (or use your IDE's auto-import).
3. Run unit tests with `./mvnw test` or `./mvnw verify` (to validate rules with JaCoCo).
4. Start SQL Server instance and create the necessary models.
5. Start application with `SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run`.

## Run integration tests with embedded Karate

`./mvnw test -Dtest=KarateRunner -DargLine=-Dkarate.env={ENVIRONMENT}`

## Package architecture

This project has been designed taking the principles of clean architecture into account. More specifically it uses 
a variety known as Hexagonal Architecture (also known as "Ports and Adapter"). More information can be found in the
resources linked below:

- [Clean Architecture by Bob Martin](https://blog.cleancoder.com/uncle-bob/2011/11/22/Clean-Architecture.html)
- [The Clean Architecture by Bob Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Clean Micro-Service Architecture by Bob Martin](https://blog.cleancoder.com/uncle-bob/2014/10/01/CleanMicroserviceArchitecture.html)
- [Buckpal: Example implementation of a Hexagonal Architecture](https://github.com/thombergs/buckpal)

## Swagger

- Swagger UI at [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)
- OAPI (v3) definition at [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)

## Health check probe

`curl -fsS http://localhost:8080/actuator/health`

## Start SQL Server instance

```shell
docker run \
  --name mssql \
  -d \
  -e "ACCEPT_EULA=Y" \
  -e "SA_PASSWORD=myP455w0rd" \
  -p 1433:1433 \
  mcr.microsoft.com/mssql/server:2017-latest
```

Using your favourite SQL client, execute the following:

```sql
CREATE SCHEMA EXCEL;

CREATE TABLE master.EXCEL.ACCOUNTS_BATCH
(
    ID          bigint IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    SOURCE_NAME varchar(250)                 NOT NULL,
    CONTENT     varbinary( max)              NOT NULL,
    CREATED_AT  datetime DEFAULT (getDate()) NOT NULL,
    UPDATED_AT  datetime DEFAULT (getDate()) NOT NULL
);
```

## Build

```shell
docker build -t java8-spring-boot-excel:latest --no-cache .
```

## Run docker

```shell
docker run --name excel --rm -it \
  -m 512m \
  --cpus="0.3" \
  --link mssql \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e DATASOURCE_URL="jdbc:sqlserver://mssql:1433;databaseName=master" \
  java8-spring-boot-excel:latest
```

## Examples included

- [x] Multipart endpoint to upload files
- [x] Base64 encoded file inside JSON
- [x] XLS file reading
- [x] Self validation
- [x] Generation of file with fixed-length lines
- [x] Saving (potentially zipped) data to DB
- [ ] Unit tests
- [x] Integration tests
