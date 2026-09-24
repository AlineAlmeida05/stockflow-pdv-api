package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;

public record EvolucaoVendaResponse(

        String data,

        BigDecimal total

) {
}