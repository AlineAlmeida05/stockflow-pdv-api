package br.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CardapioProdutoResponse(

        UUID id,

        String nome,

        String categoria,

        BigDecimal preco,

        Integer estoque,

        String imagemUrl

) {
}