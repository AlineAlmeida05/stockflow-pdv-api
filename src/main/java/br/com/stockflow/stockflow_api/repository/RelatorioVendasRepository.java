package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.dto.relatorio.*;
import br.com.stockflow.stockflow_api.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RelatorioVendasRepository extends Repository<Venda, UUID> {

    @Query("""
        SELECT new br.com.stockflow.stockflow_api.dto.relatorio.ResumoVendas(
            COUNT(v),
            COALESCE(SUM(v.valorTotal), 0.0),
            COALESCE(CAST(AVG(v.valorTotal) AS java.math.BigDecimal), 0.0),
            NULL)
        FROM Venda v
        WHERE v.status <> 'cancelada'
          AND v.tenant.id = :tenantId
          AND v.dataVenda BETWEEN :inicio AND :fim
    """)
    ResumoVendas findSummary(UUID tenantId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT new br.com.stockflow.stockflow_api.dto.relatorio.MelhorDia(
            CAST(v.dataVenda AS LocalDate), SUM(v.valorTotal))
        FROM Venda v
        WHERE v.status <> 'cancelada' AND v.tenant.id = :tenantId
          AND v.dataVenda BETWEEN :inicio AND :fim
        GROUP BY CAST(v.dataVenda AS LocalDate)
        ORDER BY SUM(v.valorTotal) DESC
    """)
    List<MelhorDia> findBestDays(UUID tenantId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("""
        SELECT new br.com.stockflow.stockflow_api.dto.relatorio.ReceitaPorFormaPagamento(
            v.formaPagamento, SUM(v.valorTotal))
        FROM Venda v
        WHERE v.status <> 'cancelada' AND v.tenant.id = :tenantId
          AND v.dataVenda BETWEEN :inicio AND :fim
        GROUP BY v.formaPagamento
        ORDER BY SUM(v.valorTotal) DESC
    """)
    List<ReceitaPorFormaPagamento> findRevenueByPaymentMethod(UUID tenantId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT new br.com.stockflow.stockflow_api.dto.relatorio.EvolucaoVendas(
            CAST(v.dataVenda AS LocalDate), SUM(v.valorTotal))
        FROM Venda v
        WHERE v.status <> 'cancelada' AND v.tenant.id = :tenantId
          AND v.dataVenda BETWEEN :inicio AND :fim
        GROUP BY CAST(v.dataVenda AS LocalDate)
        ORDER BY CAST(v.dataVenda AS date)
    """)
    List<EvolucaoVendas> findSalesEvolution(UUID tenantId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT new br.com.stockflow.stockflow_api.dto.relatorio.TopProduto(
            iv.produtoNome, SUM(iv.quantidade))
        FROM ItemVenda iv JOIN iv.venda v
        WHERE v.status <> 'cancelada' AND v.tenant.id = :tenantId
          AND v.dataVenda BETWEEN :inicio AND :fim
        GROUP BY iv.produtoNome
        ORDER BY SUM(iv.quantidade) DESC
    """)
    List<TopProduto> findTopProducts(UUID tenantId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("""
        SELECT new br.com.stockflow.stockflow_api.dto.relatorio.VendaHistorico(
            v.dataVenda, v.formaPagamento, v.quantidadeItens, v.valorTotal)
        FROM Venda v
        WHERE v.tenant.id = :tenantId
          AND v.dataVenda BETWEEN :inicio AND :fim
          AND (:formaPagamento IS NULL OR v.formaPagamento = :formaPagamento)
        ORDER BY v.dataVenda DESC
    """)
    Page<VendaHistorico> findHistory(UUID tenantId, LocalDateTime inicio, LocalDateTime fim,
                                     String formaPagamento, Pageable pageable);
}