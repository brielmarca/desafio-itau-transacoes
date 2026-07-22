package com.brielmarca.desafio.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Define as informações gerais exibidas na documentação OpenAPI da aplicação.
 */
@Configuration
public class ConfiguracaoOpenApi {

	/**
	 * Monta os metadados que identificam a API no Swagger UI.
	 *
	 * @return definição OpenAPI com título, descrição e versão
	 */
	@Bean
	OpenAPI documentacaoDaApi() {
		return new OpenAPI().info(new Info()
				.title("API de Transações")
				.description("API REST do desafio técnico do Itaú Unibanco")
				.version("1.0.0"));
	}
}
