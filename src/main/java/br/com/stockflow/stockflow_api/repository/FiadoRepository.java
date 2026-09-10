package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Fiado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FiadoRepository
        extends JpaRepository<Fiado, UUID> {

    List<Fiado> findByTenantId(
            UUID tenantId);

    List<Fiado> findByClienteId(
            UUID clienteId);

    List<Fiado> findByTenantIdAndStatus(
            UUID tenantId,
            String status);

    List<Fiado> findByClienteIdAndTenantId(
            UUID clienteId,
            UUID tenantId);

}