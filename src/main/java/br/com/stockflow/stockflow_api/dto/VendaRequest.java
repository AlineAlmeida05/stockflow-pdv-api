package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class VendaRequest {

    private String formaPagamento;

    private UUID clienteId;

    private BigDecimal valorRecebido;

    private List<ItemVendaRequest> itens;

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(
            String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(
            UUID clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(
            BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public List<ItemVendaRequest> getItens() {
        return itens;
    }

    public void setItens(
            List<ItemVendaRequest> itens) {
        this.itens = itens;
    }
}
