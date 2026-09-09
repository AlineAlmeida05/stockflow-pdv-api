package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemVendaResponse {

    private UUID produtoId;

    private String produtoNome;

    private Integer quantidade;

    private BigDecimal subtotal;

    private Boolean promocaoAplicada;

    public ItemVendaResponse(
            UUID produtoId,
            String produtoNome,
            Integer quantidade,
            BigDecimal subtotal,
            Boolean promocaoAplicada) {

        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
        this.promocaoAplicada = promocaoAplicada;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Boolean getPromocaoAplicada() {
        return promocaoAplicada;
    }
}
