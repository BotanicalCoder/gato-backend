package com.example.gato;

import com.example.gato.config.AppMailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(AppMailProperties.class)
public class GatoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatoApplication.class, args);
	}

}
