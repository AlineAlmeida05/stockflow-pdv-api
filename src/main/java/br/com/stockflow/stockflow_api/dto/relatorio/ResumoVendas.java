package br.com.stockflow.stockflow_api.dto.relatorio;


import java.math.BigDecimal;

public record ResumoVendas(
        long totalVendas,
        BigDecimal faturamento,
        BigDecimal ticketMedio,
        String formaMaisUtilizada
) {}

