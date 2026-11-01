package br.repository;

import br.entity.Venda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendaRepository
        extends JpaRepository<Venda, UUID> {

    List<Venda> findByTenantId(
            UUID tenantId
    );

    Optional<Venda> findByIdAndTenantId(
            UUID id,
            UUID tenantId
    );

}