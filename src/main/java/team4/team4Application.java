package team4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class team4Application {

	public static void main(String[] args) {
		
		Dotenv dotenv = Dotenv.load();
        System.setProperty("SPRING_MAIL_PASSWORD", dotenv.get("SPRING_MAIL_PASSWORD"));
		
		SpringApplication.run(team4Application.class, args);
	}

// 1/12 dwe
	// 1/12 lin-1

}
