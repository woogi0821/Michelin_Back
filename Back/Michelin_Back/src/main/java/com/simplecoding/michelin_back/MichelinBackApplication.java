package com.simplecoding.michelin_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MichelinBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MichelinBackApplication.class, args);
    }

}
