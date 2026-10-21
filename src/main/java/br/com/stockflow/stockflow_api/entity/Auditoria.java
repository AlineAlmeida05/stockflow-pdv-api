package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria")
@Data
public class Auditoria {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID tenantId;

    private String tenantNome;

    private UUID usuarioId;

    private String usuarioNome;

    private String acao;

    private String entidade;

    private String entidadeId;

    @Column(length = 1000)
    private String descricao;

    private LocalDateTime dataHora;
}
