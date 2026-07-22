package com.brielmarca.desafio.transacao.servico;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.brielmarca.desafio.transacao.dominio.RespostaEstatistica;
import com.brielmarca.desafio.transacao.dominio.Transacao;
import com.brielmarca.desafio.transacao.repositorio.RepositorioTransacaoEmMemoria;

/**
 * Seleciona transações da janela configurada e calcula seu resumo numérico.
 */
@Service
public class ServicoEstatistica {

	private final RepositorioTransacaoEmMemoria repositorio;
	private final Clock relogio;
	private final Duration janela;

	public ServicoEstatistica(
			RepositorioTransacaoEmMemoria repositorio,
			Clock relogio,
			@Value("${estatistica.janela-segundos:60}") long janelaSegundos) {
		this.repositorio = repositorio;
		this.relogio = relogio;
		this.janela = Duration.ofSeconds(janelaSegundos);
	}

	/**
	 * Calcula estatísticas das transações compreendidas entre o limite e o instante atual.
	 *
	 * @return quantidade, soma, média, mínimo e máximo; todos zerados quando a janela está vazia
	 */
	public RespostaEstatistica calcular() {
		// O mesmo instante de referência é usado nos dois limites para evitar variações durante o cálculo.
		Instant instanteAtual = relogio.instant();
		Instant limiteDaJanela = instanteAtual.minus(janela);

		DoubleSummaryStatistics estatisticas = repositorio.listarCopia().stream()
				.filter(transacao -> estaDentroDaJanela(transacao, limiteDaJanela, instanteAtual))
				.mapToDouble(Transacao::valor)
				.summaryStatistics();

		// DoubleSummaryStatistics usa infinitos para mínimo e máximo vazios, por isso tratamos esse caso.
		if (estatisticas.getCount() == 0) {
			return RespostaEstatistica.vazia();
		}

		return new RespostaEstatistica(
				estatisticas.getCount(),
				estatisticas.getSum(),
				estatisticas.getAverage(),
				estatisticas.getMin(),
				estatisticas.getMax());
	}

	/**
	 * Verifica se a data está no intervalo fechado entre o limite e o momento atual.
	 *
	 * @param transacao transação analisada
	 * @param limiteDaJanela instante mais antigo aceito, incluído na janela
	 * @param instanteAtual instante mais recente aceito
	 * @return verdadeiro quando a transação deve participar das estatísticas
	 */
	private boolean estaDentroDaJanela(
			Transacao transacao,
			Instant limiteDaJanela,
			Instant instanteAtual) {
		Instant instanteDaTransacao = transacao.dataHora().toInstant();
		return !instanteDaTransacao.isBefore(limiteDaJanela)
				&& !instanteDaTransacao.isAfter(instanteAtual);
	}
}
