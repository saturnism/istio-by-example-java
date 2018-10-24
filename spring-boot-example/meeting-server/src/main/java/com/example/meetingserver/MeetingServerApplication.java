package com.example.meetingserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MeetingServerApplication {
	private static Logger logger = LoggerFactory.getLogger(MeetingServerApplication.class);

	@GetMapping("/meet")
	public void meeting() {
		logger.info("Meeting for work!");
		try {
			Thread.sleep(250L);
		}
		catch (InterruptedException e) {
			// do nothing
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(MeetingServerApplication.class, args);
	}
}
