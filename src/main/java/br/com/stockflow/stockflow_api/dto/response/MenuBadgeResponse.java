package br.com.stockflow.stockflow_api.dto.response;

public record MenuBadgeResponse(
        String modulo,
        Integer totalPendencias
) {
}