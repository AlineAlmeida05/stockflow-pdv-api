package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FiadoResponse {

    private UUID id;

    private UUID clienteId;

    private String clienteNome;

    private UUID vendaId;

    private BigDecimal valorTotal;

    private LocalDateTime dataLancamento;

    private String status;

    private String observacao;

    public FiadoResponse(
            UUID id,
            UUID clienteId,
            String clienteNome,
            UUID vendaId,
            BigDecimal valorTotal,
            LocalDateTime dataLancamento,
            String status,
            String observacao) {

        this.id = id;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.vendaId = vendaId;
        this.valorTotal = valorTotal;
        this.dataLancamento = dataLancamento;
        this.status = status;
        this.observacao = observacao;
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

    public UUID getVendaId() {
        return vendaId;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataLancamento() {
        return dataLancamento;
    }

    public String getStatus() {
        return status;
    }

    public String getObservacao() {
        return observacao;
    }
}