package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "marketing_campaign")
@Data
public class MarketingCampaign {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private String tom;

    private String tipoPromocao;

    private String elementoVisual;

    @Column(columnDefinition = "TEXT")
    private String contextoProduto;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    @Column(columnDefinition = "TEXT")
    private String legenda;

    @Column(columnDefinition = "TEXT")
    private String textoWhatsapp;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }

    }

}