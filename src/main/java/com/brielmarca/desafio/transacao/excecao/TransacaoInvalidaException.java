package com.brielmarca.desafio.transacao.excecao;

/**
 * Sinaliza que uma transação viola uma regra de negócio do desafio.
 */
public class TransacaoInvalidaException extends RuntimeException {

	public TransacaoInvalidaException(String mensagem) {
		super(mensagem);
	}
}
