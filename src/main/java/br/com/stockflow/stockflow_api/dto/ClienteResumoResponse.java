package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ClienteResumoResponse {

    private UUID clienteId;

    private String nome;

    private BigDecimal limiteCredito;

    private BigDecimal saldoDevedor;

    private BigDecimal creditoDisponivel;

    private String status;

    private Integer diasSemPagamento;

    public ClienteResumoResponse(
            UUID clienteId,
            String nome,
            BigDecimal limiteCredito,
            BigDecimal saldoDevedor,
            BigDecimal creditoDisponivel,
            String status,
            Integer diasSemPagamento) {

        this.clienteId = clienteId;
        this.nome = nome;
        this.limiteCredito = limiteCredito;
        this.saldoDevedor = saldoDevedor;
        this.creditoDisponivel = creditoDisponivel;
        this.status = status;
        this.diasSemPagamento = diasSemPagamento;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public BigDecimal getSaldoDevedor() {
        return saldoDevedor;
    }

    public BigDecimal getCreditoDisponivel() {
        return creditoDisponivel;
    }

    public String getStatus() {
        return status;
    }

    public Integer getDiasSemPagamento() {
        return diasSemPagamento;
    }

}