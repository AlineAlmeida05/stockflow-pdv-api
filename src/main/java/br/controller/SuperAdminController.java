package br.controller;

import br.dto.response.ImpersonacaoResponse;
import br.repository.TenantRepository;
import br.security.JwtService;
import br.security.UsuarioAutenticadoService;
import br.service.AuditoriaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.entity.Perfil;
import br.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RestController
@RequestMapping("/super-admin")
public class SuperAdminController {

    private final JwtService jwtService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final TenantRepository tenantRepository;
    private final AuditoriaService auditoriaService;

    public SuperAdminController(
            JwtService jwtService,
            UsuarioAutenticadoService usuarioAutenticadoService,
            TenantRepository tenantRepository,
            AuditoriaService auditoriaService
    ) {
        this.jwtService = jwtService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.tenantRepository = tenantRepository;
        this.auditoriaService = auditoriaService;
    }

    @PostMapping("/impersonar/{tenantId}")
    public ImpersonacaoResponse impersonar(
            @PathVariable UUID tenantId
    ) {

        Usuario usuario =
                usuarioAutenticadoService
                        .contextoObrigatorio()
                        .usuario();

        if (usuarioAutenticadoService.estaImpersonando()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Já existe uma impersonação ativa."
            );

        }

        validarSuperAdmin(usuario);

        var tenant = tenantRepository.findById(tenantId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tenant não encontrado."
                        )
                );

        String token =
                jwtService.gerarTokenImpersonacao(
                        usuario,
                        tenantId.toString()
                );

        auditoriaService.registrar(
                null,
                null,
                usuario.getId(),
                usuario.getNome(),
                "INICIO_IMPERSONACAO",
                "Tenant",
                tenant.getId().toString(),
                "SUPER_ADMIN iniciou impersonação do tenant "
                        + tenant.getCodigoTenant()
        );

        return new ImpersonacaoResponse(
                token,
                tenantId,
                true
        );

    }

    @PostMapping("/encerrar-impersonacao")
    public ImpersonacaoResponse encerrarImpersonacao() {

        Usuario usuario =
                usuarioAutenticadoService
                        .contextoObrigatorio()
                        .usuario();

        if (!usuarioAutenticadoService.estaImpersonando()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não existe impersonação ativa."
            );

        }

        validarSuperAdmin(usuario);

        String token = jwtService.gerarToken(usuario);

        auditoriaService.registrar(
                null,
                null,
                usuario.getId(),
                usuario.getNome(),
                "FIM_IMPERSONACAO",
                "Tenant",
                usuarioAutenticadoService
                        .tenantIdAtual()
                        .toString(),
                "SUPER_ADMIN encerrou impersonação"
        );

        return new ImpersonacaoResponse(
                token,
                usuario.getTenant().getId(),
                false
        );

    }

    private void validarSuperAdmin(Usuario usuario) {

        if (usuario.getPerfil() != Perfil.SUPER_ADMIN) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas SUPER_ADMIN pode utilizar impersonação."
            );

        }

    }
}