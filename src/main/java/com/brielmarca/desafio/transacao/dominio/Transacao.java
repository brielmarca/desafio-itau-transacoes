package com.brielmarca.desafio.transacao.dominio;

import java.time.OffsetDateTime;

/**
 * Representa os dados de uma transação recebida pela API e mantida em memória.
 *
 * @param valor valor numérico informado pelo cliente
 * @param dataHora data e hora da transação, incluindo seu deslocamento de fuso horário
 */
public record Transacao(Double valor, OffsetDateTime dataHora) {
}
