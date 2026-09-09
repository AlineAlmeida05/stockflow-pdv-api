package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "item_venda")
@Data
public class ItemVenda {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "produto_nome", nullable = false)
    private String produtoNome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "promocao_aplicada", nullable = false)
    private Boolean promocaoAplicada;
}