package br.com.stockflow.stockflow_api.dto.relatorio;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EvolucaoVendas(
        LocalDate data,
        BigDecimal valor
) {}
