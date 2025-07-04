# Используем официальный образ Java 17
FROM eclipse-temurin:17-jre-alpine

# Создаем директорию для приложения
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/*.jar app.jar

# Открываем порт, который использует приложение
EXPOSE 8080

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "LandScaping_WebSite-0.0.1-SNAPSHOT.jar"]