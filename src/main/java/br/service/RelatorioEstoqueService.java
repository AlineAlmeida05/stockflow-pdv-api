package br.service;

import br.dto.response.relatorio.*;
import br.repository.ProdutoRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import br.repository.MovimentacaoEstoqueRepository;

@Service
public class RelatorioEstoqueService {
    private final ProdutoRepository produtoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public RelatorioEstoqueService(
            ProdutoRepository produtoRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.produtoRepository = produtoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public DashboardEstoque gerarDashboard() {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        var produtos =
                produtoRepository
                        .findByTenantId(
                                tenantId
                        );

        var movimentacoes =
                movimentacaoEstoqueRepository
                        .findByTenantId(
                                tenantId
                        );

        long totalProdutos =
                produtos.size();

        long semEstoque =
                produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual() == 0
                        )
                        .count();

        long estoqueBaixo =
                produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual() > 0
                                                &&
                                                produto.getEstoqueAtual()
                                                        <= produto.getEstoqueMinimo()
                        )
                        .count();

        long reposicao =
                produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual()
                                                <= produto.getEstoqueMinimo()
                        )
                        .count();

        var produtosMovimentados =
                movimentacoes.stream()
                        .map(
                                movimentacao ->
                                        movimentacao
                                                .getProduto()
                                                .getId()
                        )
                        .collect(
                                java.util.stream.Collectors
                                        .toSet()
                        );

        var produtosSemMovimentacao =
                produtos.stream()
                        .filter(
                                produto ->
                                        !produtosMovimentados.contains(
                                                produto.getId()
                                        )
                        )
                        .map(
                                produto ->
                                        new ProdutoSemMovimentacao(
                                                produto.getId(),
                                                produto.getNome()
                                        )
                        )
                        .toList();

        var sugestoesReposicao =
                produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual()
                                                <= produto.getEstoqueMinimo()
                        )
                        .map(
                                produto ->
                                        new SugestaoReposicao(

                                                produto.getId(),

                                                produto.getNome(),

                                                produto.getEstoqueAtual(),

                                                produto.getEstoqueMinimo(),

                                                produto.getEstoqueMinimo()
                                                        - produto.getEstoqueAtual()

                                        )
                        )
                        .sorted(
                                java.util.Comparator
                                        .comparing(
                                                SugestaoReposicao
                                                        ::estoqueAtual
                                        )
                        )
                        .toList();

        var giroEstoque =
                produtos.stream()
                        .map(produto -> {

                            int comprado =
                                    movimentacoes.stream()
                                            .filter(m ->
                                                    m.getProduto()
                                                            .getId()
                                                            .equals(
                                                                    produto.getId()
                                                            )
                                            )
                                            .filter(m ->
                                                    "entrada".equals(
                                                            m.getTipo()
                                                    )
                                            )
                                            .mapToInt(
                                                    m ->
                                                            m.getQuantidade()
                                            )
                                            .sum();

                            int vendido =
                                    movimentacoes.stream()
                                            .filter(m ->
                                                    m.getProduto()
                                                            .getId()
                                                            .equals(
                                                                    produto.getId()
                                                            )
                                            )
                                            .filter(m ->
                                                    "saida".equals(
                                                            m.getTipo()
                                                    )
                                            )
                                            .mapToInt(
                                                    m ->
                                                            m.getQuantidade()
                                            )
                                            .sum();

                            int giro =
                                    comprado == 0
                                            ? 0
                                            : Math.round(
                                            (
                                                    vendido
                                                    * 100f
                                            )
                                            /
                                            comprado
                                    );

                            return new GiroProduto(

                                    produto.getNome(),

                                    comprado,

                                    vendido,

                                    giro

                            );

                        })
                        .toList();

        var produtosMaiorGiro =
                giroEstoque.stream()
                        .filter(
                                produto ->
                                        produto.giro() > 0
                        )
                        .sorted(
                                java.util.Comparator
                                        .comparing(
                                                GiroProduto
                                                        ::giro
                                        )
                                        .reversed()
                        )
                        .limit(5)
                        .toList();

        var produtosMenorGiro =
                giroEstoque.stream()
                        .filter(
                                produto ->
                                        produto.giro() > 0
                        )
                        .sorted(
                                java.util.Comparator
                                        .comparing(
                                                GiroProduto
                                                        ::giro
                                        )
                        )
                        .limit(5)
                        .toList();

        var produtosSemEstoque =
                produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual() == 0
                        )
                        .map(
                                produto ->
                                        new ProdutoSemEstoque(
                                                produto.getId(),
                                                produto.getNome()
                                        )
                        )
                        .toList();

        var produtosEstoqueBaixo =
                produtos.stream()
                        .filter(
                                produto ->
                                        produto.getEstoqueAtual() > 0
                                                &&
                                                produto.getEstoqueAtual()
                                                        <= produto.getEstoqueMinimo()
                        )
                        .map(
                                produto ->
                                        new ProdutoEstoqueBaixo(

                                                produto.getId(),

                                                produto.getNome(),

                                                produto.getEstoqueAtual(),

                                                produto.getEstoqueMinimo()

                                        )
                        )
                        .toList();

        return new DashboardEstoque(

                new ResumoEstoque(
                        totalProdutos,
                        semEstoque,
                        estoqueBaixo,
                        reposicao
                ),

                produtosSemMovimentacao,

                sugestoesReposicao,

                giroEstoque,

                produtosMaiorGiro,

                produtosMenorGiro,

                produtosSemEstoque,

                produtosEstoqueBaixo

        );


    }

}