package com.example.meetingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MeetingServerApplication {
	@GetMapping("/meet")
	public void meeting() {
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
