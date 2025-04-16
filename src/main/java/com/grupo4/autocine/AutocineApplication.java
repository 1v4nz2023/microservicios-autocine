package com.grupo4.autocine;

import com.grupo4.autocine.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutocineApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(AutocineApplication.class);
		application.addInitializers(new DotenvConfig());
		application.run(args);
	}

}
