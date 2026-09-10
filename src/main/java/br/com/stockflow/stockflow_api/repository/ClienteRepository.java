package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface ClienteRepository
        extends JpaRepository<Cliente, UUID> {

    List<Cliente> findByTenantIdAndAtivoTrue(
            UUID tenantId);

    Optional<Cliente> findByIdAndTenantId(
            UUID id,
            UUID tenantId);

    Optional<Cliente> findByIdAndTenantIdAndAtivoTrue(
            UUID id,
            UUID tenantId);
}