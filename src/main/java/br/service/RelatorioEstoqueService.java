package br.service;

import br.dto.response.relatorio.ProdutoSemMovimentacao;
import br.dto.response.relatorio.SugestaoReposicao;
import br.repository.ProdutoRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import br.dto.response.relatorio.DashboardEstoque;
import br.dto.response.relatorio.ResumoEstoque;
import br.entity.Usuario;
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

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        UUID tenantId =
                usuario.getTenant().getId();

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

        return new DashboardEstoque(

                new ResumoEstoque(
                        totalProdutos,
                        semEstoque,
                        estoqueBaixo,
                        reposicao
                ),

                produtosSemMovimentacao,

                sugestoesReposicao

        );


    }

}