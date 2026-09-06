package br.com.stockflow.stockflow_api.dto;

public record LoginRequest(

                String email,

                String senha,

                String slug

) {
}