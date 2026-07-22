package com.brielmarca.desafio.transacao.dominio;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Transporta o resumo das transações recentes mantendo os nomes exigidos no JSON externo.
 *
 * @param quantidade quantidade de transações consideradas
 * @param soma soma dos valores
 * @param media média dos valores
 * @param minimo menor valor
 * @param maximo maior valor
 */
public record RespostaEstatistica(
		@JsonProperty("count") long quantidade,
		@JsonProperty("sum") double soma,
		@JsonProperty("avg") double media,
		@JsonProperty("min") double minimo,
		@JsonProperty("max") double maximo) {

	/**
	 * Cria a resposta neutra usada quando nenhuma transação pertence à janela.
	 *
	 * @return estatística com todos os campos zerados
	 */
	public static RespostaEstatistica vazia() {
		return new RespostaEstatistica(0, 0.0, 0.0, 0.0, 0.0);
	}
}
