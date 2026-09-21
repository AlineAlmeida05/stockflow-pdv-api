package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class VendaResponse {

    private UUID id;

    private LocalDateTime dataVenda;

    private String formaPagamento;

    private BigDecimal valorTotal;

    private Integer quantidadeItens;

    private String clienteNome;

    private String status;

    private String usuarioNome;

    public VendaResponse(
            UUID id,
            LocalDateTime dataVenda,
            String formaPagamento,
            BigDecimal valorTotal,
            Integer quantidadeItens,
            String clienteNome,
            String status,
            String usuarioNome) {

        this.id = id;
        this.dataVenda = dataVenda;
        this.formaPagamento = formaPagamento;
        this.valorTotal = valorTotal;
        this.quantidadeItens = quantidadeItens;
        this.clienteNome = clienteNome;
        this.status = status;
        this.usuarioNome = usuarioNome;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Integer getQuantidadeItens() {
        return quantidadeItens;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public String getStatus() {
        return status;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }
}