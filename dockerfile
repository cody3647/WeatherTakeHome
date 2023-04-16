# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 as build
COPY pom.xml ./
COPY src ./src
RUN mvn clean package


FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=build /WeatherTakeHome.jar ./
COPY 2022.csv ./

CMD ["java", "-jar", "WeatherTakeHome.jar", "2022.csv"]