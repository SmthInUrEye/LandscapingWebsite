# Используем легкий образ с Java 17
FROM openjdk:17-jdk-slim

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем собранный jar из локальной папки target в контейнер
COPY target/LandScaping_WebSite-0.0.1-SNAPSHOT.jar app.jar
COPY target/classes/liquibase /app/classes/liquibase

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
