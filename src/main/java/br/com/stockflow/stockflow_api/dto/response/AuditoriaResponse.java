package br.com.stockflow.stockflow_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditoriaResponse {

    private UUID id;

    private String tenantNome;

    private String usuarioNome;

    private String acao;

    private String entidade;

    private String entidadeId;

    private String descricao;

    private LocalDateTime dataHora;

}