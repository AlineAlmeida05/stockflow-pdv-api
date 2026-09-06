package br.com.stockflow.stockflow_api.dto;

public record AlterarSenhaRequest(

        String senhaAtual,

        String novaSenha,

        String confirmarSenha

) {
}