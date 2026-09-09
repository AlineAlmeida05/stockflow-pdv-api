package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PagamentoResponse {

    private UUID id;

    private UUID clienteId;

    private String clienteNome;

    private BigDecimal valorPago;

    private LocalDateTime dataPagamento;

    private String observacao;

    private String usuarioNome;

    private String formaPagamento;

    public PagamentoResponse(
            UUID id,
            UUID clienteId,
            String clienteNome,
            BigDecimal valorPago,
            String usuarioNome,
            LocalDateTime dataPagamento,
            String observacao,
            String formaPagamento) {

        this.id = id;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.valorPago = valorPago;
        this.usuarioNome = usuarioNome;
        this.dataPagamento = dataPagamento;
        this.observacao = observacao;
        this.usuarioNome = usuarioNome;
        this.formaPagamento = formaPagamento;
    }

    public UUID getId() {

        return id;
    }

    public UUID getClienteId() {

        return clienteId;
    }

    public String getClienteNome() {

        return clienteNome;
    }

    public BigDecimal getValorPago() {

        return valorPago;
    }

    public LocalDateTime getDataPagamento() {

        return dataPagamento;
    }

    public String getObservacao() {

        return observacao;
    }

    public String getUsuarioNome() {

        return usuarioNome;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }
}