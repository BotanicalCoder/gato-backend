package com.example.gato;

import org.springframework.boot.SpringApplication;

public class TestGatoApplication {

	public static void main(String[] args) {
		SpringApplication.from(GatoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
