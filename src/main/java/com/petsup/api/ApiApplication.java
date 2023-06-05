package com.petsup.api;

import com.petsup.api.service.UsuarioService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Timer;

@EnableFeignClients
@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);

		Timer timer = new Timer();
		UsuarioService usuarioService = new UsuarioService();

		timer.schedule(usuarioService, 0, 86400000);
	}

}
