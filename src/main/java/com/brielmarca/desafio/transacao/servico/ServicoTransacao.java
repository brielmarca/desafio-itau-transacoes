package com.brielmarca.desafio.transacao.servico;

import java.time.Clock;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.brielmarca.desafio.transacao.dominio.Transacao;
import com.brielmarca.desafio.transacao.excecao.TransacaoInvalidaException;
import com.brielmarca.desafio.transacao.repositorio.RepositorioTransacaoEmMemoria;

/**
 * Aplica as regras de negócio das transações antes de enviá-las ao repositório.
 */
@Service
public class ServicoTransacao {

	private final RepositorioTransacaoEmMemoria repositorio;
	private final Clock relogio;

	public ServicoTransacao(RepositorioTransacaoEmMemoria repositorio, Clock relogio) {
		this.repositorio = repositorio;
		this.relogio = relogio;
	}

	/**
	 * Valida e armazena uma transação recebida.
	 *
	 * @param transacao dados convertidos do JSON da requisição
	 * @throws TransacaoInvalidaException quando valor ou data estão ausentes, o valor é negativo
	 *         ou a data está no futuro
	 */
	public void receber(Transacao transacao) {
		validarPreenchimento(transacao);

		// Valores não finitos não representam quantias válidas e prejudicariam as estatísticas.
		if (!Double.isFinite(transacao.valor()) || transacao.valor() < 0) {
			throw new TransacaoInvalidaException("O valor deve ser maior ou igual a zero");
		}

		// O instante é obtido uma única vez para manter a comparação temporal consistente.
		OffsetDateTime instanteAtual = OffsetDateTime.now(relogio);
		if (transacao.dataHora().isAfter(instanteAtual)) {
			throw new TransacaoInvalidaException("A data e hora não podem estar no futuro");
		}

		repositorio.salvar(transacao);
	}

	/**
	 * Confere os campos obrigatórios antes de acessar seus valores.
	 *
	 * @param transacao objeto recebido pelo controlador
	 * @throws TransacaoInvalidaException quando o corpo ou um campo obrigatório está ausente
	 */
	private void validarPreenchimento(Transacao transacao) {
		if (transacao == null || transacao.valor() == null || transacao.dataHora() == null) {
			throw new TransacaoInvalidaException("Valor e dataHora são obrigatórios");
		}
	}
}
