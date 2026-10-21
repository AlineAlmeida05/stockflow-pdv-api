package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.response.AuditoriaResponse;
import br.com.stockflow.stockflow_api.entity.Auditoria;
import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;
import br.com.stockflow.stockflow_api.repository.AuditoriaRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;


    public AuditoriaService(
            AuditoriaRepository auditoriaRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {

        this.auditoriaRepository =
                auditoriaRepository;

        this.usuarioAutenticadoService =
                usuarioAutenticadoService;

    }

    public void registrar(
            UUID tenantId,
            String tenantNome,
            UUID usuarioId,
            String usuarioNome,
            String acao,
            String entidade,
            String entidadeId,
            String descricao) {

        Auditoria auditoria = new Auditoria();

        auditoria.setTenantId(tenantId);
        auditoria.setTenantNome(tenantNome);
        auditoria.setUsuarioId(usuarioId);
        auditoria.setUsuarioNome(usuarioNome);
        auditoria.setAcao(acao);
        auditoria.setEntidade(entidade);
        auditoria.setEntidadeId(entidadeId);
        auditoria.setDescricao(descricao);
        auditoria.setDataHora(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
    }

    public List<AuditoriaResponse> listar() {

        Usuario usuarioLogado =
                obterUsuarioLogado();

        validarAcessoAuditoria(
                usuarioLogado
        );

        List<Auditoria> auditorias;

        if (
                usuarioLogado.getPerfil()
                        == Perfil.SUPER_ADMIN
        ) {

            auditorias =
                    auditoriaRepository
                            .findTop500ByOrderByDataHoraDesc();

        } else {

            auditorias =
                    auditoriaRepository
                            .findTop100ByTenantIdOrderByDataHoraDesc(
                                    usuarioLogado
                                            .getTenant()
                                            .getId()
                            );

        }

        return auditorias
                .stream()
                .map(this::montarResponse)
                .toList();

    }

    private AuditoriaResponse montarResponse(
            Auditoria auditoria
    ) {

        return AuditoriaResponse
                .builder()
                .id(auditoria.getId())
                .tenantNome(
                        auditoria.getTenantNome()
                )
                .usuarioNome(
                        auditoria.getUsuarioNome()
                )
                .acao(
                        auditoria.getAcao()
                )
                .entidade(
                        auditoria.getEntidade()
                )
                .entidadeId(
                        auditoria.getEntidadeId()
                )
                .descricao(
                        auditoria.getDescricao()
                )
                .dataHora(
                        auditoria.getDataHora()
                )
                .build();

    }

    private void validarAcessoAuditoria(
            Usuario usuario
    ) {

        if (
                usuario.getPerfil()
                        != Perfil.SUPER_ADMIN
                        &&
                        usuario.getPerfil()
                                != Perfil.PROPRIETARIO
        ) {

            throw new RegraNegocioException(
                    "Você não possui permissão para acessar a auditoria."
            );

        }

    }

    private Usuario obterUsuarioLogado() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );

        }

        return usuarioLogado;

    }
}