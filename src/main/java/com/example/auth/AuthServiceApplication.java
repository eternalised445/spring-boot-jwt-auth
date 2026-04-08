package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AuthServiceApplication — The entry point of our Spring Boot application.
 *
 * @SpringBootApplication is a shortcut annotation that enables:
 *   - @Configuration       : marks this class as a source of bean definitions
 *   - @EnableAutoConfiguration : Spring Boot auto-configures beans based on classpath
 *   - @ComponentScan       : scans all classes in com.example.auth and sub-packages
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        // Bootstraps the entire Spring application context
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("✅ Auth Service is up and running on http://localhost:8080");
    }
}
