package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponse(

        UUID id,

        String nome,

        String telefone,

        Boolean ativo,

        LocalDateTime dataCadastro,

        BigDecimal limiteCredito,

        String observacao

) {
}