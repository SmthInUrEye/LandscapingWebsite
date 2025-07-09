package org.homeplant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
@Bean

public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
             .allowedOrigins(
             "http://localhost",          // Для фронта в Docker
             "http://localhost:80",       // Альтернативный вариант
             "http://localhost:63342",    // Для разработки в WebStorm
             "http://127.0.0.1",          // IPv4 альтернатива
             "http://frontend"            // Для обращения из других контейнеров
            )
             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
             .allowedHeaders("*")
             .allowCredentials(true)
             .maxAge(3600);
        }
    };
}
}