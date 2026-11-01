package br.dto.response;

import java.math.BigDecimal;

public record EvolucaoVendaResponse(

        String data,

        BigDecimal total

) {
}