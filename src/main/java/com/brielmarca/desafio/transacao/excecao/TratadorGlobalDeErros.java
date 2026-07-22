package com.brielmarca.desafio.transacao.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte erros conhecidos em respostas HTTP vazias, preservando o contrato do desafio.
 */
@RestControllerAdvice
public class TratadorGlobalDeErros {

	/**
	 * Trata violações das regras de valor e data da transação.
	 *
	 * @param excecao erro lançado pelo serviço durante a validação
	 * @return resposta 422 sem corpo
	 */
	@ExceptionHandler(TransacaoInvalidaException.class)
	public ResponseEntity<Void> tratarTransacaoInvalida(TransacaoInvalidaException excecao) {
		// O desafio diferencia regra de negócio inválida de JSON sintaticamente inválido.
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).build();
	}

	/**
	 * Trata JSON malformado ou campos que não podem ser convertidos para os tipos esperados.
	 *
	 * @param excecao erro de leitura produzido pelo conversor JSON do Spring
	 * @return resposta 400 sem corpo
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Void> tratarJsonInvalido(HttpMessageNotReadableException excecao) {
		// A resposta vazia evita expor detalhes internos do Jackson e atende ao contrato HTTP.
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}
