package com.brielmarca.desafio.transacao.repositorio;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Repository;

import com.brielmarca.desafio.transacao.dominio.Transacao;

/**
 * Armazena transações somente na memória e permite acesso seguro por requisições concorrentes.
 */
@Repository
public class RepositorioTransacaoEmMemoria {

	// A fila concorrente permite leituras e escritas simultâneas sem sincronização manual.
	private final Queue<Transacao> transacoes = new ConcurrentLinkedQueue<>();

	/**
	 * Inclui uma transação validada na coleção em memória.
	 *
	 * @param transacao transação validada pelo serviço
	 */
	public void salvar(Transacao transacao) {
		transacoes.add(transacao);
	}

	/**
	 * Cria uma fotografia dos dados disponíveis no momento da chamada.
	 *
	 * @return lista imutável que não expõe a coleção interna
	 */
	public List<Transacao> listarCopia() {
		// A cópia impede que outras camadas alterem acidentalmente o armazenamento interno.
		return List.copyOf(transacoes);
	}

	/**
	 * Remove todas as transações armazenadas, inclusive quando a coleção já está vazia.
	 */
	public void apagarTodas() {
		// clear é uma operação suportada pela coleção concorrente e não expõe sua referência.
		transacoes.clear();
	}
}
