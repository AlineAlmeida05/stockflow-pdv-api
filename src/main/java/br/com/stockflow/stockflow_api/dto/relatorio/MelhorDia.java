package br.com.stockflow.stockflow_api.dto.relatorio;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MelhorDia(
        LocalDate data,
        BigDecimal valor
) {}
