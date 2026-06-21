FROM eclipse-temurin:21-jre

LABEL maintainer="Rahul Agarwal"
LABEL application="payflowx-auth-service"
LABEL version="1.0"

RUN addgroup --system spring && \
    adduser --system spring --ingroup spring

WORKDIR /app

COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p logs && \
    chown -R spring:spring /app

USER spring

EXPOSE 8081

ENTRYPOINT ["java", \
"-XX:+UseContainerSupport", \
"-XX:MaxRAMPercentage=75.0", \
"-Djava.security.egd=file:/dev/./urandom", \
"-jar", \
"app.jar", \
"--spring.profiles.active=docker"]