package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "promocao")
@Data
public class Promocao {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "produto_id",
            nullable = false
    )
    private Produto produto;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precoOriginal;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precoPromocional;

    @Column(
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal percentualDesconto;

    @Column(
            nullable = false,
            length = 100
    )
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @Column(nullable = false)
    private Boolean ativa;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;

    @Column(name = "estoque_inicio")
    private Integer estoqueInicio;

    @Column(name = "meta_unidades")
    private Integer metaUnidades;

    @Column(name = "unidades_vendidas")
    private Integer unidadesVendidas;

    @Column(name = "receita_gerada")
    private BigDecimal receitaGerada;
}