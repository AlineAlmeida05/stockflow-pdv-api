package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantRepository
        extends JpaRepository<Tenant, UUID> {
}
