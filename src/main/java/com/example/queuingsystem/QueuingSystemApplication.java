package com.example.queuingsystem;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@SpringBootApplication
@Controller
public class QueuingSystemApplication {

	RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(QueuingSystemApplication.class, args);
	}

	@GetMapping("/")
	public String index(
		@RequestParam(name = "queue", defaultValue = "default") String queue,
		@RequestParam(name = "user_id") Long userId,
		HttpServletRequest request
	) {
		var cookies = request.getCookies();
		var cookieName = "user-queue-%s-token".formatted(queue);

		var token = "";
		if (cookies != null) {
			var cookie = Arrays.stream(cookies)
				.filter(i -> i.getName().equalsIgnoreCase(cookieName)).findFirst();
			token = cookie.orElse(new Cookie(cookieName, "")).getValue(); // 쿠키가 없다면 빈 쿠키 생성
		}

		URI uri = UriComponentsBuilder
			.fromUriString("http://127.0.0.1:9010")
			.path("/api/v1/queue/allowed")
			.queryParam("queue", queue)
			.queryParam("user_id", userId)
			.queryParam("token", token)
			.encode()
			.build()
			.toUri();

		ResponseEntity<AllowedUserResponse> response = restTemplate.getForEntity(uri, AllowedUserResponse.class);

		if (response.getBody() == null || !response.getBody().allowed()) {
			// 허용되지 않았다면, 대기 웹페이지로 리다이렉트
			return "redirect:http://127.0.0.1:9010/waiting-room?user_id=%d&redirect_url=%s".formatted(
				userId,
				"http://127.0.0.1:9000?user_id=%d".formatted(userId) // 현재 페이지
			);
		}

		// 허용 상태라면 해당 페이지 진입
		return "index";
	}

	public record AllowedUserResponse(
		Boolean allowed
	) {

	}
}
