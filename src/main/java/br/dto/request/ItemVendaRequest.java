package br.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ItemVendaRequest(

        @NotNull
        UUID produtoId,

        @NotNull
        @Positive
        Integer quantidade

) {
}