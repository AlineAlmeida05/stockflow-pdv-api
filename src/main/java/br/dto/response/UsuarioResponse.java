package br.dto.response;

import br.entity.Perfil;

import java.util.UUID;

public record UsuarioResponse(

        UUID id,

        String nome,

        String email,

        Perfil perfil,

        Boolean ativo,

        UUID tenantId

) {
}