package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class FiadoRequest {

    private UUID clienteId;

    private UUID vendaId;

    private BigDecimal valorTotal;

    private String observacao;

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(
            UUID clienteId) {
        this.clienteId = clienteId;
    }

    public UUID getVendaId() {
        return vendaId;
    }

    public void setVendaId(
            UUID vendaId) {
        this.vendaId = vendaId;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(
            BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao) {
        this.observacao = observacao;
    }
}
