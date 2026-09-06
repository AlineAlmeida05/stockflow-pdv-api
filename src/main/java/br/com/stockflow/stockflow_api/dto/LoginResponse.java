package br.com.stockflow.stockflow_api.dto;


public record LoginResponse(

                String id,
                String nome,
                String email,
                String perfil,
                String tenantId,
                String tenantNome,
                String token

) {
}