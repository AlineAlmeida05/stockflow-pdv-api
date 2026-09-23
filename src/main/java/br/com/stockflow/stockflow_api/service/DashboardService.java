package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.response.DashboardResponse;
import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.FiadoRepository;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.repository.VendaRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final FiadoRepository fiadoRepository;
    private final VendaRepository vendaRepository;

    public DashboardService(
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            FiadoRepository fiadoRepository,
            VendaRepository vendaRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.fiadoRepository = fiadoRepository;
        this.vendaRepository = vendaRepository;
    }

    public DashboardResponse obterDashboard() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Tenant tenant =
                usuarioLogado.getTenant();

        Integer totalProdutos =
                produtoRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .size();

        Integer produtosComEstoqueBaixo =
                (int) produtoRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual()
                                                <= produto.getEstoqueMinimo()
                        )
                        .count();

        Integer produtosSemEstoque =
                (int) produtoRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual() == 0
                        )
                        .count();

        Integer promocoesAtivas =
                (int) produtoRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .stream()
                        .filter(
                                produto ->
                                        Boolean.TRUE.equals(
                                                produto.getPromocaoAtiva()
                                        )
                        )
                        .count();

        Integer clientesDevedores =
                (int) fiadoRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .stream()
                        .map(
                                fiado ->
                                        fiado.getCliente()
                                                .getId()
                        )
                        .distinct()
                        .count();

        BigDecimal fiadosEmAberto =
                fiadoRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .stream()
                        .map(
                                fiado ->
                                        fiado.getValorTotal()
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        Integer totalVendas =
                vendaRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .size();

        BigDecimal faturamento =
                vendaRepository
                        .findByTenantId(
                                tenant.getId()
                        )
                        .stream()
                        .map(
                                venda ->
                                        venda.getValorTotal()
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return new DashboardResponse(
                totalProdutos,
                totalVendas,
                faturamento,
                fiadosEmAberto,
                clientesDevedores,
                produtosComEstoqueBaixo,
                produtosSemEstoque,
                promocoesAtivas
        );

    }

}