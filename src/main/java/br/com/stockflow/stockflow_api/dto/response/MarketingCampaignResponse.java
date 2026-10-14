package br.com.stockflow.stockflow_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record MarketingCampaignResponse(

        UUID id,

        String produtoNome,

        String nomeEmpresa,

        String tom,

        String tipoPromocao,

        String elementoVisual,

        String contextoProduto,

        String prompt,

        String legenda,

        String textoWhatsapp,

        LocalDateTime criadoEm

) {
}