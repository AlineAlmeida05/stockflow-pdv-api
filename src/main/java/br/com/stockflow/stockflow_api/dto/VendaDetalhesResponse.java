package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class VendaDetalhesResponse {

    private UUID id;

    private LocalDateTime dataVenda;

    private String formaPagamento;

    private BigDecimal valorTotal;

    private Integer quantidadeItens;

    private String clienteNome;

    private String status;

    private String motivoCancelamento;

    private LocalDateTime dataCancelamento;

    private String usuarioCancelamento;

    private String usuarioNome;

    private List<ItemVendaResponse> itens;

    public VendaDetalhesResponse(
            UUID id,
            LocalDateTime dataVenda,
            String formaPagamento,
            BigDecimal valorTotal,
            Integer quantidadeItens,
            String clienteNome,
            String status,
            String motivoCancelamento,
            LocalDateTime dataCancelamento,
            String usuarioCancelamento,
            String usuarioNome,
            List<ItemVendaResponse> itens) {

        this.id = id;
        this.dataVenda = dataVenda;
        this.formaPagamento = formaPagamento;
        this.valorTotal = valorTotal;
        this.quantidadeItens = quantidadeItens;
        this.clienteNome = clienteNome;
        this.status = status;
        this.motivoCancelamento = motivoCancelamento;
        this.dataCancelamento = dataCancelamento;
        this.usuarioCancelamento = usuarioCancelamento;
        this.itens = itens;
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

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public String getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public List<ItemVendaResponse> getItens() {
        return itens;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }
}