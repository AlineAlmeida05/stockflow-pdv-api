package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.response.TenantResponse;
import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.repository.TenantRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;
import br.com.stockflow.stockflow_api.dto.request.TenantRequest;

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

    public TenantResponse salvar(
            TenantRequest request) {

        Tenant tenant = new Tenant();

        tenant.setNome(request.nome());
        tenant.setResponsavel(request.responsavel());
        tenant.setEmail(request.email());
        tenant.setCidade(request.cidade());
        tenant.setAtivo(request.ativo());
        tenant.setSlug(request.slug());
        tenant.setCodigoTenant(request.codigoTenant());
        tenant.setLogoUrl(request.logoUrl());
        tenant.setFaviconUrl(request.faviconUrl());
        tenant.setCorPrimaria(request.corPrimaria());
        tenant.setCorSecundaria(request.corSecundaria());

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

                    throw new RegraNegocioException(
                            "Já existe um tenant com este código."
                    );

                });

        tenantRepository
                .findBySlug(
                        tenant.getSlug()
                )
                .ifPresent(t -> {

                    throw new RegraNegocioException(
                            "Já existe um tenant com este slug."
                    );

                });

        return montarResponse(
                tenantRepository.save(tenant)
        );

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
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        ));

        if (

                tenant.getSlug() != null

                        &&

                        tenant.getSlug()

                                .equalsIgnoreCase("stockflowpdv")

        ) {

            throw new RegraNegocioException(
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

    public TenantResponse atualizar(
            UUID id,
            TenantRequest request) {

        validarSuperAdmin();

        Tenant tenant = tenantRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        ));


        tenantRepository
                .findBySlug(
                        request.slug()
                )
                .ifPresent(existente -> {

                    if (
                            !existente.getId()
                                    .equals(tenant.getId())
                    ) {

                        throw new RegraNegocioException(
                                "Já existe um tenant com este slug."
                        );

                    }

                });

        tenantRepository
                .findByCodigoTenant(
                        request.codigoTenant()
                )
                .ifPresent(existente -> {

                    if (
                            !existente.getId()
                                    .equals(tenant.getId())
                    ) {

                        throw new RegraNegocioException(
                                "Já existe um tenant com este código."
                        );

                    }

                });

        tenant.setNome(request.nome());
        tenant.setResponsavel(request.responsavel());
        tenant.setEmail(request.email());
        tenant.setCidade(request.cidade());
        tenant.setAtivo(request.ativo());
        tenant.setSlug(request.slug());
        tenant.setCodigoTenant(request.codigoTenant());
        tenant.setLogoUrl(request.logoUrl());
        tenant.setFaviconUrl(request.faviconUrl());
        tenant.setCorPrimaria(request.corPrimaria());
        tenant.setCorSecundaria(request.corSecundaria());

        return montarResponse(
                tenantRepository.save(tenant)
        );
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

            throw new RegraNegocioException(
                    "Acesso permitido apenas para SUPER_ADMIN."
            );

        }

    }

    private TenantResponse montarResponse(
            Tenant tenant
    ) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getNome(),
                tenant.getCodigoTenant(),
                tenant.getAtivo()
        );
    }

}
