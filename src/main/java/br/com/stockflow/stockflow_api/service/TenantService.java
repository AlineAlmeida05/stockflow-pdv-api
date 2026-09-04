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
                return tenantRepository.save(tenant);
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
