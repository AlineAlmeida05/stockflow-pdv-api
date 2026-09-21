package br.com.stockflow.stockflow_api.dto.request;

import java.util.UUID;

public record ItemVendaRequest(

        UUID produtoId,

        Integer quantidade

) {
}