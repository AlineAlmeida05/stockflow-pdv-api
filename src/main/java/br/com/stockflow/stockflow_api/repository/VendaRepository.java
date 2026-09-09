package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Venda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VendaRepository
        extends JpaRepository<Venda, UUID> {

    List<Venda> findByTenantId(
            UUID tenantId);

}