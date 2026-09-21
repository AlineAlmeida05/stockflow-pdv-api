package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.PromocaoMetrics;
import br.com.stockflow.stockflow_api.dto.PromocaoRequest;

import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.entity.Promocao;
import br.com.stockflow.stockflow_api.entity.Usuario;

import br.com.stockflow.stockflow_api.exception.RegraNegocioException;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.repository.PromocaoRepository;

import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

import br.com.stockflow.stockflow_api.dto.ProdutoPromocaoResponse;
import br.com.stockflow.stockflow_api.repository.MovimentacaoEstoqueRepository;
import br.com.stockflow.stockflow_api.entity.MovimentacaoEstoque;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import br.com.stockflow.stockflow_api.dto.PromocaoPainelResponse;


@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private final ProdutoRepository produtoRepository;

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public PromocaoService(
            PromocaoRepository promocaoRepository,
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {

        this.promocaoRepository = promocaoRepository;

        this.produtoRepository = produtoRepository;

        this.usuarioAutenticadoService = usuarioAutenticadoService;

        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;

    }

    public List<Promocao> listar() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        return promocaoRepository
                .findByTenantAndAtivaTrue(
                        usuarioLogado.getTenant()
                );
    }

    public List<Promocao> listarAtivas() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        return promocaoRepository
                .findByTenantAndAtivaTrue(
                        usuarioLogado.getTenant()
                );
    }

    public Promocao buscarPorId(
            UUID id) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Promocao promocao =
                promocaoRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RegraNegocioException(
                                        "Promoção não encontrada."
                                )
                        );

        if (!promocao
                .getTenant()
                .getId()
                .equals(
                        usuarioLogado
                                .getTenant()
                                .getId())) {

            throw new RegraNegocioException(
                    "Você não possui acesso a esta promoção."
            );
        }

        return promocao;
    }

    public Promocao criarPromocao(
            PromocaoRequest request) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Produto produto =
                produtoRepository
                        .findById(
                                request.getProdutoId()
                        )
                        .orElseThrow(
                                () -> new RegraNegocioException(
                                        "Produto não encontrado."
                                )
                        );

        if (!produto
                .getTenant()
                .getId()
                .equals(
                        usuarioLogado
                                .getTenant()
                                .getId()
                )) {

            throw new RegraNegocioException(
                    "Você não possui acesso a este produto."
            );
        }

        if (Boolean.FALSE.equals(
                produto.getAtivo())) {

            throw new RegraNegocioException(
                    "Não é possível criar promoção para produto inativo."
            );
        }


        Promocao promocao =
                new Promocao();

        Integer estoqueInicio =
                produto.getEstoqueAtual();

        Integer metaUnidades =
                (int) Math.ceil(
                        estoqueInicio * 0.5
                );

        promocao.setEstoqueInicio(
                estoqueInicio
        );

        promocao.setMetaUnidades(
                metaUnidades
        );

        promocao.setUnidadesVendidas(
                0
        );

        promocao.setReceitaGerada(
                BigDecimal.ZERO
        );

        promocao.setProduto(
                produto);

        promocao.setPrecoOriginal(
                produto.getPrecoVenda());

        promocao.setPrecoPromocional(
                request.getPrecoPromocional());

        promocao.setPercentualDesconto(
                request.getPercentualDesconto());

        promocao.setMotivo(
                request.getMotivo());

        promocao.setDataInicio(
                LocalDateTime.now());

        promocao.setDataCriacao(
                LocalDateTime.now());

        promocao.setAtiva(true);

        promocao.setTenant(
                usuarioLogado.getTenant());

        produto.setPromocaoAtiva(
                true);

        produto.setPrecoPromocional(
                request.getPrecoPromocional());

        produto.setPromocaoMotivo(
                request.getMotivo());

        produto.setDataInicioPromocao(
                LocalDate.now());

        produto.setDataFimPromocao(
                null);

        produtoRepository.save(
                produto);

        return promocaoRepository
                .save(promocao);


    }

    public void finalizarMetaAtingida(
            Promocao promocao,
            Produto produto) {

        System.out.println(

                "META ATINGIDA: "

                        + produto.getNome()

        );

        promocao.setAtiva(false);

        promocao.setDataFim(
                LocalDateTime.now()
        );

        produto.setPromocaoAtiva(false);

        produto.setPrecoPromocional(null);

        produto.setDataInicioPromocao(null);

        produto.setDataFimPromocao(null);

        produto.setPromocaoMotivo(
                "Meta atingida");

        produtoRepository.save(produto);

        promocaoRepository.save(promocao);
    }

    public Promocao encerrarPromocao(
            UUID id) {

        Promocao promocao =
                buscarPorId(id);

        promocao.setAtiva(false);

        promocao.setDataFim(
                LocalDateTime.now());

        Produto produto =
                promocao.getProduto();

        produto.setPromocaoAtiva(false);

        produto.setPrecoPromocional(
                null);

        produto.setPromocaoMotivo(
                null);

        produto.setDataFimPromocao(
                LocalDate.now());

        produtoRepository.save(
                produto);

        return promocaoRepository.save(
                promocao);
    }

    public List<Promocao> listarPorProduto(
            UUID produtoId) {

        Produto produto =
                produtoRepository
                        .findById(produtoId)
                        .orElseThrow(
                                () -> new RegraNegocioException(
                                        "Produto não encontrado."
                                )
                        );

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (!produto.getTenant()
                .getId()
                .equals(
                        usuarioLogado
                                .getTenant()
                                .getId()
                )) {

            throw new RegraNegocioException(
                    "Você não possui acesso a este produto."
            );
        }

        return promocaoRepository
                .findByProduto(produto);
    }

    public List<ProdutoPromocaoResponse>
    listarCandidatos() {

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        List<Produto> produtos =
                produtoRepository
                        .findByTenantId(
                                usuario
                                        .getTenant()
                                        .getId()
                        );

        List<MovimentacaoEstoque> movimentacoes =
                movimentacaoEstoqueRepository
                        .findByTenantId(
                                usuario
                                        .getTenant()
                                        .getId()
                        );

        List<ProdutoPromocaoResponse> candidatos =
                new ArrayList<>();

        for (Produto produto : produtos) {

            long diasEstoque =
                    calcularDiasEstoque(
                            produto,
                            movimentacoes
                    );

            PromocaoMetrics metrics =
                    calcularMetrics(
                            produto,
                            movimentacoes,
                            diasEstoque
                    );

            boolean candidatoPromocao =
                    deveSerCandidatoPromocao(
                            produto,
                            metrics.diasEstoque(),
                            metrics.percentualGiro()
                    );

            if (candidatoPromocao) {

                BigDecimal precoPromocional =
                        calcularPrecoPromocional(
                                produto.getPrecoVenda(),
                                metrics.percentualDesconto()
                        );

                candidatos.add(
                        montarProdutoPromocao(
                                produto,
                                metrics,
                                produto.getPromocaoAtiva(),
                                precoPromocional
                        )
                );

            }

        }

        return candidatos;

    }

    public PromocaoPainelResponse
    listarPainel() {
        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        List<MovimentacaoEstoque> movimentacoes =
                movimentacaoEstoqueRepository
                        .findByTenantId(
                                usuario
                                        .getTenant()
                                        .getId()
                        );

        List<ProdutoPromocaoResponse> ativas =
                new ArrayList<>();

        for (Promocao promocao : listarAtivas()) {

            Produto produto =
                    promocao.getProduto();

            PromocaoMetrics metrics =
                    calcularMetricsBasicas(
                            produto,
                            movimentacoes
                    );

            PromocaoMetrics metricsPromocao =
                    new PromocaoMetrics(
                            metrics.quantidadeComprada(),
                            metrics.quantidadeVendida(),
                            0,
                            0,
                            promocao.getMotivo(),
                            "ATIVA",
                            promocao.getPercentualDesconto()
                                    .intValue(),
                            promocao.getMetaUnidades()
                    );

            ativas.add(
                    montarProdutoPromocao(
                            produto,
                            metricsPromocao,
                            true,
                            promocao.getPrecoPromocional()
                    )
            );


        }

        return new PromocaoPainelResponse(
                listarCandidatos(),
                ativas
        );

    }

    private int calcularQuantidadeComprada(
            UUID produtoId,
            List<MovimentacaoEstoque> movimentacoes
    ) {

        return movimentacoes
                .stream()
                .filter(
                        mov ->
                                mov.getProduto()
                                        .getId()
                                        .equals(produtoId)
                )
                .filter(
                        mov ->
                                "entrada".equalsIgnoreCase(
                                        mov.getTipo()
                                )
                )
                .mapToInt(
                        MovimentacaoEstoque::getQuantidade
                )
                .sum();

    }

    private int calcularQuantidadeVendida(
            UUID produtoId,
            List<MovimentacaoEstoque> movimentacoes
    ) {

        return movimentacoes
                .stream()
                .filter(
                        mov ->
                                mov.getProduto()
                                        .getId()
                                        .equals(produtoId)
                )
                .filter(
                        mov ->
                                "saida".equalsIgnoreCase(
                                        mov.getTipo()
                                )
                )
                .mapToInt(
                        MovimentacaoEstoque::getQuantidade
                )
                .sum();

    }

    private BigDecimal calcularReceitaPotencial(
            BigDecimal precoPromocional,
            Integer metaSugestao
    ) {

        return precoPromocional.multiply(
                BigDecimal.valueOf(
                        metaSugestao
                )
        );

    }

    private BigDecimal calcularEconomiaUnitaria(
            BigDecimal precoOriginal,
            BigDecimal precoPromocional
    ) {

        return precoOriginal.subtract(
                precoPromocional
        );

    }

    private BigDecimal calcularImpactoFinanceiro(
            BigDecimal economiaUnitaria,
            Integer estoqueAtual
    ) {

        return economiaUnitaria.multiply(
                BigDecimal.valueOf(
                        estoqueAtual
                )
        );

    }

    private ProdutoPromocaoResponse montarProdutoPromocao(
            Produto produto,
            PromocaoMetrics metrics,
            Boolean promocaoAtiva,
            BigDecimal precoPromocional
    ) {

        BigDecimal receitaPotencial =
                calcularReceitaPotencial(
                        precoPromocional,
                        metrics.metaSugestao()
                );

        BigDecimal economiaUnitaria =
                calcularEconomiaUnitaria(
                        produto.getPrecoVenda(),
                        precoPromocional
                );

        BigDecimal impactoFinanceiro =
                calcularImpactoFinanceiro(
                        economiaUnitaria,
                        produto.getEstoqueAtual()
                );

        boolean promocaoEficiente =
                calcularPromocaoEficiente(
                        metrics.percentualGiro()
                );

        String descricaoPromocao =
                calcularDescricaoPromocao(
                        metrics.diasEstoque(),
                        metrics.percentualGiro()
                );

        return new ProdutoPromocaoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getEstoqueAtual(),
                metrics.percentualGiro(),
                (int) metrics.diasEstoque(),
                promocaoAtiva,
                metrics.prioridade(),
                metrics.motivo(),
                metrics.percentualDesconto(),
                metrics.metaSugestao(),
                precoPromocional,
                receitaPotencial,
                economiaUnitaria,
                impactoFinanceiro,
                metrics.quantidadeComprada(),
                metrics.quantidadeVendida(),
                promocaoEficiente,
                descricaoPromocao
        );

    }

    private String calcularMotivo(
            long diasEstoque,
            int percentualGiro
    ) {

        if (diasEstoque >= 30
                && percentualGiro < 40) {

            return "Crítico";

        }

        if (diasEstoque >= 30) {

            return "Estoque Parado";

        }

        return "Baixo Giro";

    }

    private int calcularPercentualDesconto(
            String motivo
    ) {

        if ("Crítico".equals(motivo)) {

            return 20;

        }

        if ("Estoque Parado".equals(motivo)) {

            return 15;

        }

        return 10;

    }

    private String calcularPrioridade(
            int percentualGiro
    ) {

        return percentualGiro < 40
                ? "ALTA"
                : "MEDIA";

    }

    private int calcularMetaSugestao(
            Integer estoqueAtual
    ) {

        return (int) Math.ceil(
                estoqueAtual * 0.5
        );

    }

    private BigDecimal calcularPrecoPromocional(
            BigDecimal precoVenda,
            Integer percentualDesconto
    ) {

        return precoVenda
                .multiply(
                        BigDecimal.valueOf(
                                100 - percentualDesconto
                        )
                )
                .divide(
                        BigDecimal.valueOf(100)
                );

    }

    private boolean deveSerCandidatoPromocao(
            Produto produto,
            long diasEstoque,
            int percentualGiro
    ) {

        return Boolean.TRUE.equals(
                produto.getAtivo()
        )
                && produto.getEstoqueAtual() > 0
                && !Boolean.TRUE.equals(
                produto.getPromocaoAtiva()
        )
                && (
                diasEstoque >= 30
                        || percentualGiro < 40
        );

    }

    private PromocaoMetrics calcularMetricsBasicas(
            Produto produto,
            List<MovimentacaoEstoque> movimentacoes
    ) {

        int quantidadeComprada =
                calcularQuantidadeComprada(
                        produto.getId(),
                        movimentacoes
                );

        int quantidadeVendida =
                calcularQuantidadeVendida(
                        produto.getId(),
                        movimentacoes
                );

        int percentualGiro =
                calcularPercentualGiro(
                        quantidadeComprada,
                        quantidadeVendida
                );

        return new PromocaoMetrics(
                quantidadeComprada,
                quantidadeVendida,
                percentualGiro,
                0,
                "",
                "",
                0,
                0
        );

    }

    private PromocaoMetrics calcularMetrics(
            Produto produto,
            List<MovimentacaoEstoque> movimentacoes,
            long diasEstoque
    ) {

        PromocaoMetrics basicas =
                calcularMetricsBasicas(
                        produto,
                        movimentacoes
                );

        String motivo =
                calcularMotivo(
                        diasEstoque,
                        basicas.percentualGiro()
                );

        return new PromocaoMetrics(
                basicas.quantidadeComprada(),
                basicas.quantidadeVendida(),
                basicas.percentualGiro(),
                diasEstoque,
                motivo,
                calcularPrioridade(
                        basicas.percentualGiro()
                ),
                calcularPercentualDesconto(
                        motivo
                ),
                calcularMetaSugestao(
                        produto.getEstoqueAtual()
                )
        );

    }

    private long calcularDiasEstoque(
            Produto produto,
            List<MovimentacaoEstoque> movimentacoes
    ) {

        LocalDateTime ultimaEntrada =
                movimentacoes
                        .stream()
                        .filter(
                                mov ->
                                        mov.getProduto()
                                                .getId()
                                                .equals(
                                                        produto.getId()
                                                )
                        )
                        .filter(
                                mov ->
                                        "entrada".equalsIgnoreCase(
                                                mov.getTipo()
                                        )
                        )
                        .map(
                                MovimentacaoEstoque::getDataMovimentacao
                        )
                        .max(
                                LocalDateTime::compareTo
                        )
                        .orElse(null);

        if (ultimaEntrada == null) {

            return 0;

        }

        return ChronoUnit.DAYS.between(
                ultimaEntrada.toLocalDate(),
                LocalDate.now()
        );

    }

    private int calcularPercentualGiro(
            int quantidadeComprada,
            int quantidadeVendida
    ) {

        if (quantidadeComprada <= 0) {

            return 0;

        }

        return (quantidadeVendida * 100)
                / quantidadeComprada;

    }

    private boolean calcularPromocaoEficiente(
            int percentualGiro
    ) {

        return percentualGiro >= 70;

    }

    private String calcularDescricaoPromocao(
            long diasEstoque,
            int percentualGiro
    ) {

        if (
                diasEstoque >= 30
                        && percentualGiro < 40
        ) {

            return String.format(
                    "Produto com baixo giro (%d%%) e %d dias em estoque.",
                    percentualGiro,
                    diasEstoque
            );

        }

        if (diasEstoque >= 30) {

            return String.format(
                    "Produto parado há %d dias.",
                    diasEstoque
            );

        }

        return String.format(
                "Giro baixo (%d%% das unidades vendidas).",
                percentualGiro
        );

    }
}