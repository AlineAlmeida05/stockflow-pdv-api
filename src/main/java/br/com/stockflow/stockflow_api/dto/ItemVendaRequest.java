package br.com.stockflow.stockflow_api.dto;

import java.util.UUID;

public class ItemVendaRequest {

    private UUID produtoId;

    private Integer quantidade;

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(
            UUID produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(
            Integer quantidade) {
        this.quantidade = quantidade;
    }
    
}