# Используем легкий образ с Java 17
FROM openjdk:17-jdk-slim

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем собранный jar из локальной папки target в контейнер
COPY target/*.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
