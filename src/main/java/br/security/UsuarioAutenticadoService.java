package br.security;

import br.entity.Tenant;
import br.entity.Usuario;
import br.repository.TenantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UsuarioAutenticadoService {

    public UsuarioContexto contextoAtual() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UsuarioContexto contexto) {
            return contexto;
        }

        return null;
    }

    public Usuario usuarioLogado() {

        UsuarioContexto contexto = contextoAtual();

        if (contexto == null) {
            return null;
        }

        return contexto.usuario();
    }

    public UUID tenantIdAtual() {

        UsuarioContexto contexto = contextoAtual();

        if (contexto == null) {
            return null;
        }

        return contexto.tenantId();

    }

    public boolean estaImpersonando() {

        UsuarioContexto contexto = contextoAtual();

        if (contexto == null) {
            return false;
        }

        return contexto.impersonando();

    }

    public UsuarioContexto contextoObrigatorio() {

        UsuarioContexto contexto = contextoAtual();

        if (contexto == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );

        }

        return contexto;
    }

    private final TenantRepository tenantRepository;

    public UsuarioAutenticadoService(
            TenantRepository tenantRepository
    ) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant tenantAtual() {

        UUID tenantId = tenantIdAtual();

        if (tenantId == null) {

            return null;

        }

        return tenantRepository
                .findById(tenantId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Tenant não encontrado."
                        )
                );

    }

}