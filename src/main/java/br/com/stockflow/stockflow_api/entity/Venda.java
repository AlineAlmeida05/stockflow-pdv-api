package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "venda")
@Data
public class Venda {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "forma_pagamento", nullable = false)
    private String formaPagamento;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "quantidade_itens", nullable = false)
    private Integer quantidadeItens;

    @Column(nullable = false)
    private String status;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @ManyToOne
    @JoinColumn(name = "usuario_cancelamento_id")
    private Usuario usuarioCancelamento;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Column(name = "cliente_nome")
    private String clienteNome;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}