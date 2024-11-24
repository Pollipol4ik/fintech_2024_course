FROM openjdk:21
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY build/libs/FintechCourse2024-0.0.1-SNAPSHOT.jar /app/fintech.jar
CMD ["java", "-jar", "fintech.jar"]