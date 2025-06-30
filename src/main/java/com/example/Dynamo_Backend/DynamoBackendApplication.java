package com.example.Dynamo_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DynamoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamoBackendApplication.class, args);
	}

}
