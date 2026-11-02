package br.service;

import br.entity.Empresa;
import br.entity.Tenant;
import br.exception.RecursoNaoEncontradoException;
import br.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import br.dto.response.EmpresaResponse;
import br.security.UsuarioAutenticadoService;
import br.entity.Usuario;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import br.entity.Perfil;
import br.exception.RegraNegocioException;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {

        this.empresaRepository = empresaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;

    }

    public List<EmpresaResponse> listarTodas() {

        validarSuperAdmin();

        return empresaRepository.findAll()
                .stream()
                .map(this::montarResponse)
                .toList();
    }

    public EmpresaResponse obterEmpresa() {

        Empresa empresa = obterEmpresaDoUsuarioLogado();

        return montarResponse(
                empresa
        );

    }

    public Empresa obterEntidadeEmpresa() {

        return obterEmpresaDoUsuarioLogado();

    }

    private Empresa obterEmpresaDoUsuarioLogado() {

        Usuario usuarioLogado = obterUsuarioLogado();

        System.out.println(
                "TENANT LOGADO: "
                        + usuarioLogado.getTenant().getNome()
        );

        return empresaRepository
                .findByTenantId(
                        usuarioLogado.getTenant().getId()
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada."
                        )
                );

    }

    private EmpresaResponse montarResponse(
            Empresa empresa
    ) {
        Tenant tenant = empresa.getTenant();

        return new EmpresaResponse(

                empresa.getId(),

                tenant.getNome(),
                tenant.getRazaoSocial(),
                tenant.getCnpj(),

                tenant.getTelefone(),
                tenant.getEmail(),

                tenant.getEndereco(),
                tenant.getCidade(),
                tenant.getUf(),

                tenant.getResponsavel(),

                tenant.getLogoUrl(),

                empresa.getSlogan(),

                tenant.getCorPrimaria(),
                tenant.getCorSecundaria(),

                empresa.getVersaoSistema(),

                empresa.getDataImplantacao()
        );
    }

    private void validarSuperAdmin() {

        Usuario usuarioLogado = obterUsuarioLogado();

        if (usuarioLogado.getPerfil()
                != Perfil.SUPER_ADMIN) {

            throw new RegraNegocioException(
                    "Acesso permitido apenas para SUPER_ADMIN."
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