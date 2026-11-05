package br.dto.response.relatorio;

import java.math.BigDecimal;

public record ResumoFiados(

        BigDecimal valorTotalFiados,

        BigDecimal valorEmAberto,

        BigDecimal valorRecebido,

        long pendentes,

        long pagos,

        long clientesDevedores

) {}