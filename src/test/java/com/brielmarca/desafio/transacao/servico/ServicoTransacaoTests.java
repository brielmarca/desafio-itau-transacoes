package com.brielmarca.desafio.transacao.servico;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brielmarca.desafio.transacao.dominio.Transacao;
import com.brielmarca.desafio.transacao.excecao.TransacaoInvalidaException;
import com.brielmarca.desafio.transacao.repositorio.RepositorioTransacaoEmMemoria;

/**
 * Valida isoladamente as regras de negócio aplicadas pelo serviço de transações.
 */
class ServicoTransacaoTests {

	private static final Instant AGORA = Instant.parse("2026-07-22T15:00:00Z");

	private RepositorioTransacaoEmMemoria repositorio;
	private ServicoTransacao servico;

	@BeforeEach
	void configurar() {
		repositorio = new RepositorioTransacaoEmMemoria();
		servico = new ServicoTransacao(repositorio, Clock.fixed(AGORA, ZoneOffset.UTC));
	}

	@Test
	void deveArmazenarTransacaoNoInstanteAtual() {
		servico.receber(new Transacao(10.0, OffsetDateTime.ofInstant(AGORA, ZoneOffset.UTC)));

		assertThat(repositorio.listarCopia()).hasSize(1);
	}

	@Test
	void deveRecusarCamposAusentes() {
		assertThatThrownBy(() -> servico.receber(new Transacao(null, null)))
				.isInstanceOf(TransacaoInvalidaException.class);
	}

	@Test
	void deveRecusarValorNegativo() {
		Transacao transacao = new Transacao(-1.0, OffsetDateTime.ofInstant(AGORA, ZoneOffset.UTC));

		assertThatThrownBy(() -> servico.receber(transacao))
				.isInstanceOf(TransacaoInvalidaException.class);
	}

	@Test
	void deveRecusarDataFutura() {
		Transacao transacao = new Transacao(
				1.0, OffsetDateTime.ofInstant(AGORA.plusSeconds(1), ZoneOffset.UTC));

		assertThatThrownBy(() -> servico.receber(transacao))
				.isInstanceOf(TransacaoInvalidaException.class);
	}
}
