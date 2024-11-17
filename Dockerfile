FROM openjdk:21
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY target/fintech.jar /app/fintech.jar
CMD ["java", "-jar", "fintech.jar"]
