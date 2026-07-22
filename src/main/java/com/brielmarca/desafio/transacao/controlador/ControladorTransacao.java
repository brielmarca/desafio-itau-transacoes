package com.brielmarca.desafio.transacao.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brielmarca.desafio.transacao.dominio.Transacao;
import com.brielmarca.desafio.transacao.servico.ServicoTransacao;

/**
 * Expõe as operações HTTP relacionadas ao recebimento de transações.
 */
@RestController
@RequestMapping(path = "/transacao", produces = MediaType.APPLICATION_JSON_VALUE)
public class ControladorTransacao {

	private final ServicoTransacao servico;

	public ControladorTransacao(ServicoTransacao servico) {
		this.servico = servico;
	}

	/**
	 * Recebe o JSON, delega validação e armazenamento ao serviço e não produz conteúdo.
	 *
	 * @param transacao transação convertida do corpo JSON
	 * @return resposta 201 sem corpo quando os dados são válidos
	 * @throws com.brielmarca.desafio.transacao.excecao.TransacaoInvalidaException quando uma regra
	 *         de negócio é violada; o tratador global converte o erro em 422
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> receber(@RequestBody Transacao transacao) {
		servico.receber(transacao);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Apaga todas as transações armazenadas e não produz conteúdo de resposta.
	 *
	 * @return resposta 200 sem corpo, mesmo quando não existiam transações
	 */
	@DeleteMapping
	public ResponseEntity<Void> apagarTodas() {
		servico.apagarTodas();
		return ResponseEntity.ok().build();
	}
}
