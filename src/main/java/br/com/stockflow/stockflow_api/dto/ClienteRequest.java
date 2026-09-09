package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;

public class ClienteRequest {

    private String nome;

    private String telefone;

    private BigDecimal limiteCredito;

    private String observacao;

    public String getNome() {
        return nome;
    }

    public void setNome(
            String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(
            String telefone) {
        this.telefone = telefone;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(
            BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao) {
        this.observacao = observacao;
    }
}