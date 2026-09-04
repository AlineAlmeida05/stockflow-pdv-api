package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "tenant")
@Data

public class Tenant {

    @Id
    @GeneratedValue
    private UUID id;

    private String nome;

    private String slug;

    private String email;

    private String telefone;

    private Boolean ativo;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "razao_social")
    private String razaoSocial;

    private String cnpj;

    private String responsavel;

    private String endereco;

    private String cidade;

    private String uf;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "cor_primaria")
    private String corPrimaria;

    @Column(name = "cor_secundaria")
    private String corSecundaria;

    @Column(name = "marketing_ia_habilitado")
    private Boolean marketingIaHabilitado;

    @Column(name = "delivery_habilitado")
    private Boolean deliveryHabilitado;

    @PrePersist
    public void prePersist() {

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }

    }

}
