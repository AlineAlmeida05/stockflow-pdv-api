package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PromocaoRequest {

    private UUID produtoId;

    private BigDecimal precoPromocional;

    private BigDecimal percentualDesconto;

    private String motivo;

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(
            UUID produtoId) {
        this.produtoId = produtoId;
    }

    public BigDecimal getPrecoPromocional() {
        return precoPromocional;
    }

    public void setPrecoPromocional(
            BigDecimal precoPromocional) {
        this.precoPromocional =
                precoPromocional;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(
            BigDecimal percentualDesconto) {
        this.percentualDesconto =
                percentualDesconto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(
            String motivo) {
        this.motivo = motivo;
    }
}