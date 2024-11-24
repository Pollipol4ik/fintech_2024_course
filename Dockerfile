FROM openjdk:21
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY build/libs/*.jar /app/fintech.jar
CMD ["java", "-jar", "fintech.jar"]