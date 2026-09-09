package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PagamentoRequest {

    private UUID clienteId;

    private BigDecimal valorPago;

    private String observacao;

    private String formaPagamento;

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(
            UUID clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(
            BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao) {
        this.observacao = observacao;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(
            String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

}