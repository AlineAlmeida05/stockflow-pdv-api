package br.repository;

import br.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface TenantRepository
                extends JpaRepository<Tenant, UUID> {

        Optional<Tenant> findBySlug(
                        String slug);

        Optional<Tenant> findByCodigoTenant(
                String codigoTenant
        );

}
