package com.brielmarca.desafio.transacao;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.brielmarca.desafio.transacao.dominio.Transacao;
import com.brielmarca.desafio.transacao.repositorio.RepositorioTransacaoEmMemoria;

/**
 * Exercita o contrato HTTP completo usando o contexto Spring e o MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(ApiTransacaoIntegracaoTests.ConfiguracaoRelogioFixo.class)
class ApiTransacaoIntegracaoTests {

	private static final Instant INSTANTE_FIXO = Instant.parse("2026-07-22T15:00:00Z");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RepositorioTransacaoEmMemoria repositorio;

	@BeforeEach
	void limparRepositorio() {
		// Cada teste começa sem dados para não depender da ordem de execução.
		repositorio.apagarTodas();
	}

	@Test
	void deveReceberTransacaoValida() throws Exception {
		mockMvc.perform(post("/transacao")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"valor":123.45,"dataHora":"2026-07-22T11:59:59-03:00"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(content().string(""));

		org.assertj.core.api.Assertions.assertThat(repositorio.listarCopia()).hasSize(1);
	}

	@Test
	void deveAceitarValorZero() throws Exception {
		mockMvc.perform(post("/transacao")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"valor":0,"dataHora":"2026-07-22T12:00:00-03:00"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(content().string(""));
	}

	@Test
	void deveRecusarValorNegativo() throws Exception {
		requisicaoInvalida("""
				{"valor":-0.01,"dataHora":"2026-07-22T12:00:00-03:00"}
				""");
	}

	@Test
	void deveRecusarDataFutura() throws Exception {
		requisicaoInvalida("""
				{"valor":10,"dataHora":"2026-07-22T12:00:01-03:00"}
				""");
	}

	@Test
	void deveRecusarValorAusente() throws Exception {
		requisicaoInvalida("""
				{"dataHora":"2026-07-22T12:00:00-03:00"}
				""");
	}

	@Test
	void deveRecusarDataHoraAusente() throws Exception {
		requisicaoInvalida("""
				{"valor":10}
				""");
	}

	@Test
	void deveRetornarBadRequestParaJsonMalformado() throws Exception {
		mockMvc.perform(post("/transacao")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"valor\":10,"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(""));
	}

	@Test
	void deveRetornarBadRequestParaDataEmFormatoInvalido() throws Exception {
		mockMvc.perform(post("/transacao")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"valor":10,"dataHora":"22/07/2026 12:00"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(""));
	}

	@Test
	void deveApagarTodasAsTransacoes() throws Exception {
		repositorio.salvar(transacao(10, 5));
		repositorio.salvar(transacao(20, 10));

		mockMvc.perform(delete("/transacao"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		org.assertj.core.api.Assertions.assertThat(repositorio.listarCopia()).isEmpty();
	}

	@Test
	void devePermitirExclusaoSemTransacoes() throws Exception {
		mockMvc.perform(delete("/transacao"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));
	}

	@Test
	void deveRetornarEstatisticaZeradaSemTransacoes() throws Exception {
		mockMvc.perform(get("/estatistica"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0))
				.andExpect(jsonPath("$.sum").value(0.0))
				.andExpect(jsonPath("$.avg").value(0.0))
				.andExpect(jsonPath("$.min").value(0.0))
				.andExpect(jsonPath("$.max").value(0.0));
	}

	@Test
	void deveContabilizarUmaTransacaoRecente() throws Exception {
		repositorio.salvar(transacao(25.50, 30));

		mockMvc.perform(get("/estatistica"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1))
				.andExpect(jsonPath("$.sum").value(25.50))
				.andExpect(jsonPath("$.avg").value(25.50))
				.andExpect(jsonPath("$.min").value(25.50))
				.andExpect(jsonPath("$.max").value(25.50));
	}

	@Test
	void deveCalcularEstatisticasDeVariasTransacoes() throws Exception {
		repositorio.salvar(transacao(10, 10));
		repositorio.salvar(transacao(20, 20));
		repositorio.salvar(transacao(35, 30));

		mockMvc.perform(get("/estatistica"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(3))
				.andExpect(jsonPath("$.sum").value(65.0))
				.andExpect(jsonPath("$.avg").value(closeTo(21.666, 0.001)))
				.andExpect(jsonPath("$.min").value(10.0))
				.andExpect(jsonPath("$.max").value(35.0));
	}

	@Test
	void naoDeveContabilizarTransacaoAntiga() throws Exception {
		repositorio.salvar(transacao(100, 61));

		mockMvc.perform(get("/estatistica"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0));
	}

	@Test
	void deveIncluirTransacaoExatamenteNoLimiteDaJanela() throws Exception {
		repositorio.salvar(transacao(50, 60));

		mockMvc.perform(get("/estatistica"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1))
				.andExpect(jsonPath("$.sum").value(50.0));
	}

	@Test
	void deveZerarEstatisticasDepoisDaExclusao() throws Exception {
		repositorio.salvar(transacao(10, 10));

		mockMvc.perform(delete("/transacao"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/estatistica"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0))
				.andExpect(jsonPath("$.sum").value(0.0))
				.andExpect(jsonPath("$.avg").value(0.0))
				.andExpect(jsonPath("$.min").value(0.0))
				.andExpect(jsonPath("$.max").value(0.0));
	}

	private void requisicaoInvalida(String json) throws Exception {
		mockMvc.perform(post("/transacao")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isUnprocessableContent())
				.andExpect(content().string(""));
	}

	private Transacao transacao(double valor, long segundosAtras) {
		return new Transacao(valor, OffsetDateTime.ofInstant(
				INSTANTE_FIXO.minusSeconds(segundosAtras), ZoneOffset.UTC));
	}

	/**
	 * Substitui o relógio real por um instante determinístico durante os testes de integração.
	 */
	@TestConfiguration
	static class ConfiguracaoRelogioFixo {

		@Bean
		@Primary
		Clock relogioFixo() {
			return Clock.fixed(INSTANTE_FIXO, ZoneOffset.UTC);
		}
	}
}
