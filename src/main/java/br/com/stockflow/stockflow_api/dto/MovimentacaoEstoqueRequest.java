package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class MovimentacaoEstoqueRequest {

    private UUID produtoId;

    private String tipo;

    private Integer quantidade;

    private BigDecimal precoCompra;

    private String observacao;

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(
            UUID produtoId) {

        this.produtoId = produtoId;

    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(
            String tipo) {

        this.tipo = tipo;

    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(
            Integer quantidade) {

        this.quantidade = quantidade;

    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(
            BigDecimal precoCompra) {

        this.precoCompra = precoCompra;

    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao) {

        this.observacao = observacao;

    }

}