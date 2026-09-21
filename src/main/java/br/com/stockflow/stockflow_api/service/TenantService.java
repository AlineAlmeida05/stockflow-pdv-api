package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.TenantRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public TenantService(
            TenantRepository tenantRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.tenantRepository = tenantRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;

    }

    public List<Tenant> listarTodos() {

        validarSuperAdmin();

        return tenantRepository.findAll();

    }

    public Tenant salvar(Tenant tenant) {

        validarSuperAdmin();

        if (tenant.getCodigoTenant() == null
                ||
                tenant.getCodigoTenant().isBlank()) {

            tenant.setCodigoTenant(
                    gerarCodigoTenant(
                            tenant.getNome()));

        }

        tenantRepository
                .findByCodigoTenant(
                        tenant.getCodigoTenant()
                )
                .ifPresent(t -> {

                    throw new RuntimeException(
                            "Já existe um tenant com este código."
                    );

                });

        tenantRepository
                .findBySlug(
                        tenant.getSlug()
                )
                .ifPresent(t -> {

                    throw new RuntimeException(
                            "Já existe um tenant com este slug."
                    );

                });

        return tenantRepository.save(
                tenant);

    }

    private String gerarCodigoTenant(
            String nome) {

        String[] palavras = nome.trim()
                .split("\\s+");

        if (palavras.length >= 2) {

            String codigo = palavras[0].substring(0, 1)
                    +
                    palavras[1].substring(
                            0,
                            Math.min(
                                    2,
                                    palavras[1].length()));

            return codigo.toUpperCase();

        }

        return nome
                .substring(
                        0,
                        Math.min(
                                3,
                                nome.length()))
                .toUpperCase();

    }

    public void excluir(UUID id) {

        validarSuperAdmin();

        Tenant tenant = tenantRepository
                .findById(id)
                .orElseThrow();

        if (

                tenant.getSlug() != null

                        &&

                        tenant.getSlug()

                                .equalsIgnoreCase("stockflowpdv")

        ) {

            throw new RuntimeException(
                    "O tenant principal não pode ser desativado."
            );

        }

        tenant.setAtivo(false);

        tenantRepository.save(tenant);

    }

    public Tenant buscarPorSlug(
            String slug) {

        return tenantRepository
                .findBySlug(slug)
                .orElse(null);

    }

    public Tenant atualizar(
            UUID id,
            Tenant tenantAtualizado) {

        validarSuperAdmin();

        Tenant tenant = tenantRepository
                .findById(id)
                .orElseThrow();

        tenantRepository
                .findBySlug(
                        tenantAtualizado.getSlug()
                )
                .ifPresent(existente -> {

                    if (
                            !existente.getId()
                                    .equals(tenant.getId())
                    ) {

                        throw new RuntimeException(
                                "Já existe um tenant com este slug."
                        );

                    }

                });

        tenantRepository
                .findByCodigoTenant(
                        tenantAtualizado.getCodigoTenant()
                )
                .ifPresent(existente -> {

                    if (
                            !existente.getId()
                                    .equals(tenant.getId())
                    ) {

                        throw new RuntimeException(
                                "Já existe um tenant com este código."
                        );

                    }

                });

        tenant.setNome(
                tenantAtualizado.getNome());

        tenant.setResponsavel(
                tenantAtualizado.getResponsavel());

        tenant.setEmail(
                tenantAtualizado.getEmail());

        tenant.setCidade(
                tenantAtualizado.getCidade());

        tenant.setAtivo(
                tenantAtualizado.getAtivo());

        tenant.setSlug(
                tenantAtualizado.getSlug());

        tenant.setCodigoTenant(
                tenantAtualizado.getCodigoTenant());

        tenant.setLogoUrl(
                tenantAtualizado.getLogoUrl());

        tenant.setFaviconUrl(
                tenantAtualizado.getFaviconUrl());

        tenant.setCorPrimaria(
                tenantAtualizado.getCorPrimaria());

        tenant.setCorSecundaria(
                tenantAtualizado.getCorSecundaria());

        return tenantRepository.save(
                tenant);
    }

    private void validarSuperAdmin() {

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (
                usuario == null
                        ||
                        usuario.getPerfil()
                                != Perfil.SUPER_ADMIN
        ) {

            throw new RuntimeException(
                    "Acesso permitido apenas para SUPER_ADMIN."
            );

        }

    }

}
