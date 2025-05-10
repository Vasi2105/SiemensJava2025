
package com.siemens.internship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the main entry point of the Spring Boot application.
 * When you run this class, it launches the entire Spring framework setup:
 * it scans for components, loads configurations, and starts the embedded web server.
 */
@SpringBootApplication
public class InternshipApplication {

    /**
     * The main method simply delegates to Spring Boot’s SpringApplication.run(),
     * which triggers the full startup  process.
     */
    public static void main(String[] args) {
        SpringApplication.run(InternshipApplication.class, args);
    }  
}
