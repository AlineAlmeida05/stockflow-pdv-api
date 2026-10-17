package br.dto.response.relatorio;

import java.math.BigDecimal;

public record DevedorDTO(

        String nome,

        BigDecimal total

) {
}