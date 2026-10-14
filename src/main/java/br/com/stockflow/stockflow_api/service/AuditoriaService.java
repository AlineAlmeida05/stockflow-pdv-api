package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Auditoria;
import br.com.stockflow.stockflow_api.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(
            UUID tenantId,
            UUID usuarioId,
            String usuarioNome,
            String acao,
            String entidade,
            String entidadeId,
            String descricao) {

        Auditoria auditoria = new Auditoria();

        auditoria.setTenantId(tenantId);
        auditoria.setUsuarioId(usuarioId);
        auditoria.setUsuarioNome(usuarioNome);
        auditoria.setAcao(acao);
        auditoria.setEntidade(entidade);
        auditoria.setEntidadeId(entidadeId);
        auditoria.setDescricao(descricao);
        auditoria.setDataHora(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
    }
}