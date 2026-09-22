package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CancelarVendaRequest(

        @NotBlank(
                message = "Motivo do cancelamento é obrigatório."
        )
        String motivo



) {
}