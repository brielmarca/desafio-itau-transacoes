package com.brielmarca.desafio.transacao.controlador;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brielmarca.desafio.transacao.dominio.RespostaEstatistica;
import com.brielmarca.desafio.transacao.servico.ServicoEstatistica;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Expõe a consulta HTTP das estatísticas das transações recentes.
 */
@RestController
@RequestMapping(path = "/estatistica", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Estatísticas", description = "Resumo das transações dentro da janela configurada")
public class ControladorEstatistica {

	private final ServicoEstatistica servico;

	public ControladorEstatistica(ServicoEstatistica servico) {
		this.servico = servico;
	}

	/**
	 * Solicita o cálculo para a janela configurada e serializa o resultado como JSON.
	 *
	 * @return resposta 200 com count, sum, avg, min e max
	 */
	@GetMapping
	@Operation(summary = "Consulta as estatísticas recentes")
	@ApiResponse(responseCode = "200", description = "Estatísticas calculadas")
	public ResponseEntity<RespostaEstatistica> consultar() {
		return ResponseEntity.ok(servico.calcular());
	}
}
