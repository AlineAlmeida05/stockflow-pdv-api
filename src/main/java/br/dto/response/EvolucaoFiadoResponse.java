package br.dto.response;

import java.math.BigDecimal;

public record EvolucaoFiadoResponse(

        String data,

        BigDecimal total

) {
}