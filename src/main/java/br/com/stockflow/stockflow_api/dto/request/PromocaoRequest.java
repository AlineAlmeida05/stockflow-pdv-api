package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PromocaoRequest(

        @NotNull
        UUID produtoId

) {
}