package com.campuslostfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CampusLostFoundApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusLostFoundApplication.class, args);
    }
} 