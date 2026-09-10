package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Pagamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PagamentoRepository
        extends JpaRepository<Pagamento, UUID> {

    List<Pagamento> findByTenantId(
            UUID tenantId);

    List<Pagamento> findByClienteId(
            UUID clienteId);

    List<Pagamento> findByClienteIdAndTenantId(
            UUID clienteId,
            UUID tenantId);

}