package com.relieflink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the ReliefLink API — Disaster Relief Resource Management System.
 *
 * <p>Spring Boot auto-configures component scanning from this root package,
 * picking up all controllers, services, repositories, and configuration
 * classes under {@code com.relieflink.*}.</p>
 */
@SpringBootApplication
public class ReliefLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReliefLinkApplication.class, args);
    }
}
