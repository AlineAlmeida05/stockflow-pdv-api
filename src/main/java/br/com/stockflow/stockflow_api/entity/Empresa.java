package br.com.stockflow.stockflow_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "empresa")
@Data
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(name = "razao_social")
    private String razaoSocial;

    private String cnpj;

    private String telefone;

    private String email;

    private String endereco;

    private String cidade;

    private String uf;

    private String proprietario;

    @Column(name = "logo_url")
    private String logoUrl;

    private String slogan;

    @Column(name = "cor_primaria")
    private String corPrimaria;

    @Column(name = "cor_secundaria")
    private String corSecundaria;

    @Column(name = "versao_sistema")
    private String versaoSistema;

    @Column(name = "data_implantacao")
    private LocalDate dataImplantacao;
}