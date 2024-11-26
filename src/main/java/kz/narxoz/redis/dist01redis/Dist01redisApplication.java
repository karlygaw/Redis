package kz.narxoz.redis.dist01redis;

import jakarta.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Dist01redisApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dist01redisApplication.class, args);
	}

	@GetMapping
	public String hello() {
		return "hello";
	}

	@GetMapping("/session")
	public String getSession(HttpSession session) {
		return "session: " + session;
	}

}
