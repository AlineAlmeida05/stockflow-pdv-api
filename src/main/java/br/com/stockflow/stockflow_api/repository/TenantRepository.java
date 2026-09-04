package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface TenantRepository
                extends JpaRepository<Tenant, UUID> {

        Optional<Tenant> findBySlug(
                        String slug);
}
