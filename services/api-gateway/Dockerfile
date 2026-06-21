FROM eclipse-temurin:21-jre

LABEL maintainer="Rahul Agarwal"
LABEL application="payflowx-api-gateway"
LABEL version="1.0"

RUN addgroup --system spring && \
    adduser --system spring --ingroup spring

WORKDIR /app

COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]