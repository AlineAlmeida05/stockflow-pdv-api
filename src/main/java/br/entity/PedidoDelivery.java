package br.entity;

import br.entity.StatusPedidoDelivery;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pedido_delivery")
public class PedidoDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String numero;

    private Long sequencial;

    private String clienteNome;

    private String telefone;

    private String endereco;

    private String complemento;

    @Column(length = 500)
    private String observacao;

    private BigDecimal valorTotal;

    private String formaPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPedidoDelivery status;

    private LocalDateTime dataPedido;

    private LocalDateTime dataEntrega;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private LocalTime horaSaidaEntrega;

    private UUID vendaId;

}
