package br.entity;

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

    @OneToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private String slogan;

    @Column(name = "versao_sistema")
    private String versaoSistema;

    @Column(name = "data_implantacao")
    private LocalDate dataImplantacao;

}