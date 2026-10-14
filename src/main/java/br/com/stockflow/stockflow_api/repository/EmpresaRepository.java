package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByTenantId(
            UUID tenantId
    );
}