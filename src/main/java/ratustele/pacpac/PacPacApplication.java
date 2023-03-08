package ratustele.pacpac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PacPacApplication {
	@GetMapping("/message")
	public String message(){
		return "Hello World!";
	}
	public static void main(String[] args) {
		SpringApplication.run(PacPacApplication.class, args);
	}

}
