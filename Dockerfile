FROM eclipse-temurin:8-jdk-alpine as build-env
RUN mkdir -p /opt/app
COPY . /opt/app
WORKDIR /opt/app

RUN ./mvnw package

FROM eclipse-temurin:8-jre-alpine
RUN mkdir -p /opt/app
# COPY ./target/java8-spring-boot-excel-*.jar /opt/app/app.jar
COPY --from=build-env /opt/app/target/java8-spring-boot-excel-*.jar /opt/app/app.jar
CMD java $JAVA_OPTS -jar /opt/app/app.jar
