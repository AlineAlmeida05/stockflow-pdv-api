package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fiado")
public class Fiado {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "cliente_id",
            nullable = false)
    private Cliente cliente;

    @Column(name = "venda_id")
    private UUID vendaId;

    @Column(
            name = "valor_total",
            nullable = false,
            precision = 10,
            scale = 2)
    private BigDecimal valorTotal;

    @Column(
            name = "data_lancamento",
            nullable = false)
    private LocalDateTime dataLancamento;

    @Column(nullable = false)
    private String status;

    @Column(length = 500)
    private String observacao;

    @ManyToOne
    @JoinColumn(
            name = "tenant_id",
            nullable = false)
    private Tenant tenant;

    public Fiado() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(
            Cliente cliente) {
        this.cliente = cliente;
    }

    public UUID getVendaId() {
        return vendaId;
    }

    public void setVendaId(
            UUID vendaId) {
        this.vendaId = vendaId;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(
            BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(
            LocalDateTime dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
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

