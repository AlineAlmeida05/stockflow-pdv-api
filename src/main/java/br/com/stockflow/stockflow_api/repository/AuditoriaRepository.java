package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface AuditoriaRepository
        extends JpaRepository<Auditoria, UUID> {

    List<Auditoria> findByTenantIdOrderByDataHoraDesc(
            UUID tenantId
    );

    List<Auditoria> findTop100ByTenantIdOrderByDataHoraDesc(
            UUID tenantId
    );

    List<Auditoria> findTop500ByOrderByDataHoraDesc();

}