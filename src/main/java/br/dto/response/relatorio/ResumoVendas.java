package br.dto.response.relatorio;


import java.math.BigDecimal;

public record ResumoVendas(
        long totalVendas,
        BigDecimal faturamento,
        BigDecimal ticketMedio,
        String formaMaisUtilizada
) {}

