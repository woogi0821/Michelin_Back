package com.simplecoding.michelin_back.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.simplecoding.michelin_back")
public class JpaConfig {
}
