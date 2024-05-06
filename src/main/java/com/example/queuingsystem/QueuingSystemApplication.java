package com.example.queuingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
public class QueuingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueuingSystemApplication.class, args);
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

}
