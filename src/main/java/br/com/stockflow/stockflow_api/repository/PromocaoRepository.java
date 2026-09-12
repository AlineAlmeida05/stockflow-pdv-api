package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Promocao;
import br.com.stockflow.stockflow_api.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import br.com.stockflow.stockflow_api.entity.Produto;

public interface PromocaoRepository
        extends JpaRepository<Promocao, UUID> {

    List<Promocao> findByTenant(
            Tenant tenant
    );

    List<Promocao> findByTenantAndAtivaTrue(
            Tenant tenant
    );

    List<Promocao> findByProduto(
            Produto produto
    );

    Optional<Promocao>
    findByProdutoAndAtivaTrue(
            Produto produto);
}