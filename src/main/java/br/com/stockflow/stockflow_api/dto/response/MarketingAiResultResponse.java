package br.com.stockflow.stockflow_api.dto.response;

import java.util.List;

public record MarketingAiResultResponse(

        String legenda,

        List<String> hashtags,

        String textoWhatsapp

) {
}