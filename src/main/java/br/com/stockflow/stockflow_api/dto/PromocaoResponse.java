package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PromocaoResponse {

    private UUID id;

    private UUID produtoId;

    private String produtoNome;

    private BigDecimal precoOriginal;

    private BigDecimal precoPromocional;

    private BigDecimal percentualDesconto;

    private String motivo;

    private Boolean ativa;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    public PromocaoResponse(
            UUID id,
            UUID produtoId,
            String produtoNome,
            BigDecimal precoOriginal,
            BigDecimal precoPromocional,
            BigDecimal percentualDesconto,
            String motivo,
            Boolean ativa,
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {

        this.id = id;
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.precoOriginal = precoOriginal;
        this.precoPromocional = precoPromocional;
        this.percentualDesconto = percentualDesconto;
        this.motivo = motivo;
        this.ativa = ativa;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public BigDecimal getPrecoOriginal() {
        return precoOriginal;
    }

    public BigDecimal getPrecoPromocional() {
        return precoPromocional;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public String getMotivo() {
        return motivo;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }
}