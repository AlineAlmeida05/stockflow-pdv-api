package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(
            name = "limite_credito",
            nullable = false,
            precision = 10,
            scale = 2)
    private BigDecimal limiteCredito;

    @Column(length = 500)
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    public Cliente() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(
            LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
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

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(
            Tenant tenant) {
        this.tenant = tenant;
    }
}