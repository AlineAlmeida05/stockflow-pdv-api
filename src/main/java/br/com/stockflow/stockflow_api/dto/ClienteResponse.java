package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ClienteResponse {

    private UUID id;

    private String nome;

    private String telefone;

    private Boolean ativo;

    private LocalDateTime dataCadastro;

    private BigDecimal limiteCredito;

    private String observacao;

    public ClienteResponse(
            UUID id,
            String nome,
            String telefone,
            Boolean ativo,
            LocalDateTime dataCadastro,
            BigDecimal limiteCredito,
            String observacao) {

        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
        this.limiteCredito = limiteCredito;
        this.observacao = observacao;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public String getObservacao() {
        return observacao;
    }
}