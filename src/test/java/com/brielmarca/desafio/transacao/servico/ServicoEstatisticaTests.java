package com.brielmarca.desafio.transacao.servico;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brielmarca.desafio.transacao.dominio.RespostaEstatistica;
import com.brielmarca.desafio.transacao.dominio.Transacao;
import com.brielmarca.desafio.transacao.repositorio.RepositorioTransacaoEmMemoria;

/**
 * Verifica isoladamente o filtro temporal e os cálculos das estatísticas.
 */
class ServicoEstatisticaTests {

	private static final Instant AGORA = Instant.parse("2026-07-22T15:00:00Z");

	private RepositorioTransacaoEmMemoria repositorio;
	private ServicoEstatistica servico;

	@BeforeEach
	void configurar() {
		repositorio = new RepositorioTransacaoEmMemoria();
		Clock relogio = Clock.fixed(AGORA, ZoneOffset.UTC);
		servico = new ServicoEstatistica(repositorio, relogio, 60);
	}

	@Test
	void deveCalcularResumoSomenteDaJanela() {
		repositorio.salvar(transacao(10, 5));
		repositorio.salvar(transacao(30, 60));
		repositorio.salvar(transacao(100, 61));

		RespostaEstatistica resposta = servico.calcular();

		assertThat(resposta.quantidade()).isEqualTo(2);
		assertThat(resposta.soma()).isEqualTo(40.0);
		assertThat(resposta.media()).isEqualTo(20.0);
		assertThat(resposta.minimo()).isEqualTo(10.0);
		assertThat(resposta.maximo()).isEqualTo(30.0);
	}

	@Test
	void deveRetornarZerosQuandoNaoHaTransacoesRecentes() {
		RespostaEstatistica resposta = servico.calcular();

		assertThat(resposta).isEqualTo(RespostaEstatistica.vazia());
	}

	@Test
	void deveRespeitarJanelaConfigurada() {
		repositorio.salvar(transacao(15, 90));
		ServicoEstatistica servicoComJanelaMaior = new ServicoEstatistica(
				repositorio, Clock.fixed(AGORA, ZoneOffset.UTC), 120);

		RespostaEstatistica resposta = servicoComJanelaMaior.calcular();

		assertThat(resposta.quantidade()).isEqualTo(1);
		assertThat(resposta.soma()).isEqualTo(15.0);
	}

	private Transacao transacao(double valor, long segundosAtras) {
		return new Transacao(valor, OffsetDateTime.ofInstant(
				AGORA.minusSeconds(segundosAtras), ZoneOffset.UTC));
	}
}
