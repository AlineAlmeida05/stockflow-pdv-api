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
}
