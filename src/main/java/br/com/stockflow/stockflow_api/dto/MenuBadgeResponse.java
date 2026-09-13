package br.com.stockflow.stockflow_api.dto;

public record MenuBadgeResponse(
        String modulo,
        Integer totalPendencias
) {
}