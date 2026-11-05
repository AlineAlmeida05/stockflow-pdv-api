package br.dto.response.relatorio;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EvolucaoFiados(

        LocalDate data,

        BigDecimal valor

) {}