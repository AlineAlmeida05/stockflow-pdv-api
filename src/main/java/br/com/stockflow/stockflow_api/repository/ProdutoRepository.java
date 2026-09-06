package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProdutoRepository
                extends JpaRepository<Produto, UUID> {

        List<Produto> findByTenantId(
                        UUID tenantId);

        Produto findTopByTenantIdOrderByCodigoDesc(
                        UUID tenantId);

}