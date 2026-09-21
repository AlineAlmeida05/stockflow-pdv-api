package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TenantService {

        private final TenantRepository tenantRepository;

        public TenantService(
                        TenantRepository tenantRepository) {
                this.tenantRepository = tenantRepository;
        }

        public List<Tenant> listarTodos() {
                return tenantRepository.findAll();
        }

        public Tenant salvar(Tenant tenant) {

                if (tenant.getCodigoTenant() == null
                                ||
                                tenant.getCodigoTenant().isBlank()) {

                        tenant.setCodigoTenant(
                                        gerarCodigoTenant(
                                                        tenant.getNome()));

                }

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
                tenantRepository.deleteById(id);
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

                Tenant tenant = tenantRepository
                                .findById(id)
                                .orElseThrow();

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

}
