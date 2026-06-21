FROM eclipse-temurin:21-jre

LABEL maintainer="Rahul Agarwal"
LABEL application="payflowx-audit-service"
LABEL version="1.0"

RUN addgroup --system spring && \
    adduser --system spring --ingroup spring

WORKDIR /app

COPY target/audit-service-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8088

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]