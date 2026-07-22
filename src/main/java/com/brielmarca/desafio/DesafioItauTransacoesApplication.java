package com.brielmarca.desafio;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Inicializa a aplicação e declara dependências compartilhadas pelo contexto Spring.
 */
@SpringBootApplication
public class DesafioItauTransacoesApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioItauTransacoesApplication.class, args);
	}

	/**
	 * Fornece o relógio real usado para validar e consultar datas.
	 *
	 * @return relógio do sistema em UTC, que pode ser substituído por um relógio fixo nos testes
	 */
	@Bean
	Clock relogio() {
		// Centralizar o relógio evita chamadas estáticas difíceis de controlar em testes.
		return Clock.systemUTC();
	}

}
