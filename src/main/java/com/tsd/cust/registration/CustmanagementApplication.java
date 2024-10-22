package com.tsd.cust.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.tsd.cust.registration.repo")
@EntityScan(basePackages = "com.tsd.cust.registration.entity")
public class CustmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustmanagementApplication.class, args);
	}

	@Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	@Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
