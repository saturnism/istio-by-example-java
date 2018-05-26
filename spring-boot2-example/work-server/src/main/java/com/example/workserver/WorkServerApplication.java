package com.example.workserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@SpringBootApplication
public class WorkServerApplication {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(WorkServerApplication.class, args);
	}

	@RestController
	public static class WorkController {
		private static Logger logger = LoggerFactory.getLogger(WorkController.class);

		@Value("${endpoint:http://localhost:8081/meet}")
		private String endpoint;

		@Autowired
		private RestTemplate restTemplate;


		@GetMapping("/work")
		public String work() {
			int count = 0;
			for (int i = 0; i < 4; i++) {
				logger.info("Going to meeting: {}", i);
				ResponseEntity<Void> entity = restTemplate
						.getForEntity(URI.create(endpoint), Void.class);
				if (entity.getStatusCode() == HttpStatus.OK) {
					count++;
				}
			}

			return "went to " + count + " meetings";
		}

	}
}
