package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Entity
@Table(name = "produto")
@Data
public class Produto {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String codigo;

    private String codigoBarras;

    private String promocaoMotivo;

    private String nome;

    private String categoria;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoVenda;

    private Integer estoqueAtual;

    private Integer estoqueMinimo;

    private Boolean ativo;

    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

    private Boolean promocaoAtiva;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoPromocional;

    private LocalDate dataInicioPromocao;

    private LocalDate dataFimPromocao;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}