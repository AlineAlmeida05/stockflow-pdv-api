package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.response.*;
import br.com.stockflow.stockflow_api.entity.*;
import br.com.stockflow.stockflow_api.repository.FiadoRepository;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.repository.VendaRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import br.com.stockflow.stockflow_api.repository.ItemVendaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.stockflow.stockflow_api.repository.MovimentacaoEstoqueRepository;

@Service
public class DashboardService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final FiadoRepository fiadoRepository;
    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;


    public DashboardService(
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            FiadoRepository fiadoRepository,
            VendaRepository vendaRepository,
            ItemVendaRepository itemVendaRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository
    ) {

        this.produtoRepository = produtoRepository;
        this.usuarioAutenticadoService =
                usuarioAutenticadoService;
        this.fiadoRepository = fiadoRepository;
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository =
                itemVendaRepository;
        this.movimentacaoEstoqueRepository =
                movimentacaoEstoqueRepository;

    }

    public DashboardResponse obterDashboard(
            String periodo
    ) {

        LocalDateTime dataInicial = null;

        switch (periodo) {

            case "hoje" ->

                    dataInicial =
                            LocalDate.now()
                                    .atStartOfDay();

            case "7dias" ->

                    dataInicial =
                            LocalDate.now()
                                    .minusDays(7)
                                    .atStartOfDay();

            case "30dias" ->

                    dataInicial =
                            LocalDate.now()
                                    .minusDays(30)
                                    .atStartOfDay();

            case "mes" ->

                    dataInicial =
                            LocalDate.now()
                                    .withDayOfMonth(1)
                                    .atStartOfDay();

            default ->

                    dataInicial = null;

        }

        final LocalDateTime dataFiltro = dataInicial;

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Tenant tenant =
                usuarioLogado.getTenant();

        final List<Produto> produtos =
                produtoRepository
                        .findByTenantId(
                                tenant.getId()
                        );

        List<Venda> vendas =
                vendaRepository
                        .findByTenantId(
                                tenant.getId()
                        );

        var fiados =
                fiadoRepository
                        .findByTenantId(
                                tenant.getId()
                        );

        List<MovimentacaoEstoque> movimentacoes =
                movimentacaoEstoqueRepository
                        .findByTenantId(
                                tenant.getId()
                        );

        if (dataInicial != null) {

            vendas =
                    vendas.stream()
                            .filter(
                                    venda ->
                                            venda.getDataVenda() != null
                                                    &&
                                                    !venda.getDataVenda()
                                                            .isBefore(
                                                                    dataFiltro
                                                            )
                            )
                            .toList();

            fiados =
                    fiados.stream()
                            .filter(
                                    fiado ->
                                            fiado.getDataLancamento() != null
                                                    &&
                                                    !fiado.getDataLancamento()
                                                            .isBefore(
                                                                    dataFiltro
                                                            )
                            )
                            .toList();

            movimentacoes =
                    movimentacoes.stream()
                            .filter(
                                    mov ->
                                            mov.getDataMovimentacao() != null
                                                    &&
                                                    !mov.getDataMovimentacao()
                                                            .isBefore(
                                                                    dataFiltro
                                                            )
                            )
                            .toList();

        }

        final List<Venda> vendasFiltradas =
                vendas;

        final var fiadosFiltrados =
                fiados;

        final List<MovimentacaoEstoque> movimentacoesFiltradas =
                movimentacoes;

        Integer totalProdutos =
                produtos.size();

        Integer produtosComEstoqueBaixo =
                (int) produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual()
                                                <= produto.getEstoqueMinimo()
                        )
                        .count();

        Integer produtosSemEstoque =
                (int) produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual() == 0
                        )
                        .count();

        Integer promocoesAtivas =
                (int) produtos.stream()
                        .filter(
                                produto ->
                                        Boolean.TRUE.equals(
                                                produto.getPromocaoAtiva()
                                        )
                        )
                        .count();


        Integer clientesDevedores =
                (int) fiadosFiltrados.stream()
                        .map(
                                fiado ->
                                        fiado.getCliente()
                                                .getId()
                        )
                        .distinct()
                        .count();

        BigDecimal fiadosEmAberto =
                fiadosFiltrados.stream()
                        .map(
                                fiado ->
                                        fiado.getValorTotal()
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        Integer totalVendas =
                vendasFiltradas.size();


        BigDecimal faturamento =
                vendasFiltradas.stream()
                        .filter(
                                venda ->
                                        !"cancelada".equals(
                                                venda.getStatus()
                                        )
                        )
                        .map(
                                Venda::getValorTotal
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );



        Map<String, BigDecimal> agrupado =
                new LinkedHashMap<>();

        for (Venda venda : vendasFiltradas) {

            if ("cancelada".equals(
                    venda.getStatus()
            )) {

                continue;

            }

            String data =
                    venda.getDataVenda()
                            .toLocalDate()
                            .toString();

            BigDecimal atual =
                    agrupado.getOrDefault(
                            data,
                            BigDecimal.ZERO
                    );

            agrupado.put(
                    data,
                    atual.add(
                            venda.getValorTotal()
                    )
            );

        }

        List<EvolucaoVendaResponse>
                evolucaoVendas =

                agrupado.entrySet()
                        .stream()
                        .map(
                                entry ->
                                        new EvolucaoVendaResponse(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                        )
                        .toList();

        Map<String, BigDecimal> faturamentoAgrupado =
                new LinkedHashMap<>();

        for (Venda venda : vendasFiltradas) {

            if ("cancelada".equals(
                    venda.getStatus()
            )) {

                continue;

            }

            String formaPagamento =
                    venda.getFormaPagamento();

            BigDecimal atual =
                    faturamentoAgrupado.getOrDefault(
                            formaPagamento,
                            BigDecimal.ZERO
                    );

            faturamentoAgrupado.put(
                    formaPagamento,
                    atual.add(
                            venda.getValorTotal()
                    )
            );

        }

        List<PagamentoDashboardResponse>
                faturamentoPorPagamento =

                faturamentoAgrupado.entrySet()
                        .stream()
                        .map(
                                entry ->
                                        new PagamentoDashboardResponse(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                        )
                        .toList();

        Map<String, Integer> produtosAgrupados =
                new LinkedHashMap<>();


        for (Venda venda : vendasFiltradas) {

            if ("cancelada".equals(
                    venda.getStatus()
            )) {

                continue;

            }

            List<ItemVenda> itens =
                    itemVendaRepository
                            .findByVendaId(
                                    venda.getId()
                            );

            for (ItemVenda item : itens) {

                Integer atual =
                        produtosAgrupados
                                .getOrDefault(
                                        item.getProdutoNome(),
                                        0
                                );

                produtosAgrupados.put(
                        item.getProdutoNome(),
                        atual +
                                item.getQuantidade()
                );

            }

        }

        List<TopProdutoDashboardResponse>
                topProdutosVendidos =
                produtosAgrupados.entrySet()
                        .stream()
                        .sorted(
                                (a, b) ->
                                        Integer.compare(
                                                b.getValue(),
                                                a.getValue()
                                        )
                        )
                        .limit(5)
                        .map(
                                entry ->
                                        new TopProdutoDashboardResponse(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                        )
                        .toList();

        Map<String, BigDecimal> fiadosAgrupados =
                new LinkedHashMap<>();

        for (var fiado : fiadosFiltrados) {

            String data =
                    fiado.getDataLancamento()
                            .toLocalDate()
                            .toString();

            BigDecimal atual =
                    fiadosAgrupados.getOrDefault(
                            data,
                            BigDecimal.ZERO
                    );

            fiadosAgrupados.put(
                    data,
                    atual.add(
                            fiado.getValorTotal()
                    )
            );

        }

        List<EvolucaoFiadoResponse>
                evolucaoFiados =

                fiadosAgrupados.entrySet()
                        .stream()
                        .map(
                                entry ->
                                        new EvolucaoFiadoResponse(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                        )
                        .toList();

        Map<String, Integer> entradas =
                new LinkedHashMap<>();

        Map<String, Integer> saidas =
                new LinkedHashMap<>();

        for (MovimentacaoEstoque mov : movimentacoesFiltradas) {

            String produto =
                    mov.getProdutoNome();

            if ("entrada".equals(
                    mov.getTipo()
            )) {

                entradas.put(
                        produto,
                        entradas.getOrDefault(
                                produto,
                                0
                        ) + mov.getQuantidade()
                );

            }

            if ("saida".equals(
                    mov.getTipo()
            )) {

                saidas.put(
                        produto,
                        saidas.getOrDefault(
                                produto,
                                0
                        ) + mov.getQuantidade()
                );

            }

        }

        List<GiroEstoqueResponse>
                giroEstoque =

                entradas.entrySet()
                        .stream()
                        .map(entry -> {

                            String produto =
                                    entry.getKey();

                            Integer comprado =
                                    entry.getValue();

                            Integer vendido =
                                    saidas.getOrDefault(
                                            produto,
                                            0
                                    );

                            Integer giro =

                                    comprado == 0

                                            ? 0

                                            : Math.round(
                                            (vendido * 100f)
                                            / comprado
                                    );

                            return new GiroEstoqueResponse(
                                    produto,
                                    giro
                            );

                        })
                        .filter(
                                item ->
                                        item.giro() > 0
                        )
                        .sorted(
                                (a, b) ->
                                        Integer.compare(
                                                b.giro(),
                                                a.giro()
                                        )
                        )
                        .limit(5)
                        .toList();

        List<PromocaoEficienteResponse>
                promocoesEficientes =

                produtos.stream()

                        .filter(
                                produto ->
                                        Boolean.TRUE.equals(
                                                produto.getPromocaoAtiva()
                                        )
                        )

                        .map(produto -> {

                            Integer comprado =
                                    entradas.getOrDefault(
                                            produto.getNome(),
                                            0
                                    );

                            Integer vendido =
                                    saidas.getOrDefault(
                                            produto.getNome(),
                                            0
                                    );

                            Integer giro =

                                    comprado == 0

                                            ? 0

                                            : Math.round(
                                            (vendido * 100f)
                                            / comprado
                                    );

                            return new PromocaoEficienteResponse(
                                    produto.getNome(),
                                    giro
                            );

                        })

                        .filter(
                                promocao ->
                                        promocao.giro() >= 70
                        )

                        .sorted(
                                (a, b) ->
                                        Integer.compare(
                                                b.giro(),
                                                a.giro()
                                        )
                        )

                        .toList();

        Map<String, Integer> produtosPromocionais =
                new LinkedHashMap<>();

        for (Venda venda : vendasFiltradas) {

            if ("cancelada".equals(
                    venda.getStatus()
            )) {

                continue;

            }

            List<ItemVenda> itens =
                    itemVendaRepository
                            .findByVendaId(
                                    venda.getId()
                            );

            for (ItemVenda item : itens) {

                if (!Boolean.TRUE.equals(
                        item.getPromocaoAplicada()
                )) {

                    continue;

                }

                Integer atual =
                        produtosPromocionais
                                .getOrDefault(
                                        item.getProdutoNome(),
                                        0
                                );

                produtosPromocionais.put(
                        item.getProdutoNome(),
                        atual +
                                item.getQuantidade()
                );

            }

        }

        List<ProdutoPromocionalResponse>
                produtosPromocionaisMaisVendidos =

                produtosPromocionais.entrySet()
                        .stream()
                        .sorted(
                                (a, b) ->
                                        Integer.compare(
                                                b.getValue(),
                                                a.getValue()
                                        )
                        )
                        .limit(5)
                        .map(
                                entry ->
                                        new ProdutoPromocionalResponse(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                        )
                        .toList();

        Integer totalVendasPromocionais =
                0;

        for (Venda venda : vendasFiltradas) {

            if ("cancelada".equals(
                    venda.getStatus()
            )) {

                continue;

            }

            List<ItemVenda> itens =
                    itemVendaRepository
                            .findByVendaId(
                                    venda.getId()
                            );

            boolean possuiPromocao =

                    itens.stream()
                            .anyMatch(
                                    item ->
                                            Boolean.TRUE.equals(
                                                    item.getPromocaoAplicada()
                                            )
                            );

            if (possuiPromocao) {

                totalVendasPromocionais++;

            }

        }

        BigDecimal faturamentoPromocional =
                BigDecimal.ZERO;

        for (Venda venda : vendasFiltradas) {

            if ("cancelada".equals(
                    venda.getStatus()
            )) {

                continue;

            }

            List<ItemVenda> itens =
                    itemVendaRepository
                            .findByVendaId(
                                    venda.getId()
                            );

            for (ItemVenda item : itens) {

                if (
                        Boolean.TRUE.equals(
                                item.getPromocaoAplicada()
                        )
                ) {

                    faturamentoPromocional =
                            faturamentoPromocional.add(
                                    item.getSubtotal()
                            );

                }

            }

        }

        Integer totalPromocoesEficientes =
                promocoesEficientes.size();

        List<PromocaoBaixaEfetividadeResponse>
                promocoesBaixaEfetividade =

                produtos.stream()

                        .filter(
                                produto ->
                                        Boolean.TRUE.equals(
                                                produto.getPromocaoAtiva()
                                        )
                        )

                        .map(produto -> {

                            Integer comprado =
                                    entradas.getOrDefault(
                                            produto.getNome(),
                                            0
                                    );

                            Integer vendido =
                                    saidas.getOrDefault(
                                            produto.getNome(),
                                            0
                                    );

                            Integer giro =

                                    comprado == 0

                                            ? 0

                                            : Math.round(
                                            (vendido * 100f)
                                            / comprado
                                    );

                            return new PromocaoBaixaEfetividadeResponse(
                                    produto.getNome(),
                                    giro
                            );

                        })

                        .filter(
                                promocao ->
                                        promocao.giro() < 40
                        )

                        .sorted(
                                (a, b) ->
                                        Integer.compare(
                                                a.giro(),
                                                b.giro()
                                        )
                        )

                        .toList();

        return new DashboardResponse(

                totalProdutos,

                totalVendas,

                faturamento,

                fiadosEmAberto,

                clientesDevedores,

                produtosComEstoqueBaixo,

                produtosSemEstoque,

                promocoesAtivas,

                evolucaoVendas,

                evolucaoFiados,

                giroEstoque,

                faturamentoPorPagamento,

                topProdutosVendidos,

                promocoesEficientes,

                produtosPromocionaisMaisVendidos,

                totalVendasPromocionais,

                faturamentoPromocional,

                totalPromocoesEficientes,

                promocoesBaixaEfetividade

        );

    }

}