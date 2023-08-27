FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /home/app

COPY pom.xml pom.xml

RUN mvn dependency:go-offline

COPY src /home/app/src

RUN mvn clean test package

FROM openjdk:17

WORKDIR /app

COPY --from=builder /home/app/target/*.jar app.jar

ENV MONGODB=test
ENV MONGOHOST=host.docker.internal
ENV MONGOPASSWORD=secret
ENV MONGOPORT=27017
ENV MONGOUSER=kokud
ENV RECIPEPICKER_DOMAIN=http://10.0.2.2:8080/
ENV AUDIENCES=recipespickerdebug
ENV ISSUER=https://securetoken.google.com/recipespickerdebug

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]