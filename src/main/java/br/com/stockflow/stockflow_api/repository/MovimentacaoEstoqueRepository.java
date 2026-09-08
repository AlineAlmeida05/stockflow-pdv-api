package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.MovimentacaoEstoque;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimentacaoEstoqueRepository
                extends JpaRepository<MovimentacaoEstoque, UUID> {

        List<MovimentacaoEstoque> findByTenantId(
                        UUID tenantId);
}