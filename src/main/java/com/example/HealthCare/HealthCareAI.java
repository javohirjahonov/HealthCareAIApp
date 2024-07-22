package com.example.HealthCare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@EnableDiscoveryClient
@SpringBootApplication
public class HealthCareAI {

    public static void main(String[] args) {
        SpringApplication.run(HealthCareAI.class, args);
    }
}
