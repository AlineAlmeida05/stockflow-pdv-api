package br.service;

import br.entity.MovimentacaoEstoque;
import br.exception.RecursoNaoEncontradoException;
import br.repository.MovimentacaoEstoqueRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import br.dto.request.MovimentacaoEstoqueRequest;
import br.entity.Produto;
import br.entity.Usuario;
import br.repository.ProdutoRepository;
import br.security.UsuarioAutenticadoService;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.dto.response.MovimentacaoEstoqueResponse;
import org.springframework.transaction.annotation.Transactional;
import br.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MovimentacaoEstoqueService {

        private final MovimentacaoEstoqueRepository repository;
        private final ProdutoRepository produtoRepository;
        private final UsuarioAutenticadoService usuarioAutenticadoService;
        private final AuditoriaService auditoriaService;

        public MovimentacaoEstoqueService(
                        MovimentacaoEstoqueRepository repository,
                        ProdutoRepository produtoRepository,
                        UsuarioAutenticadoService usuarioAutenticadoService,
                        AuditoriaService auditoriaService) {

                this.repository = repository;
                this.produtoRepository = produtoRepository;
                this.usuarioAutenticadoService = usuarioAutenticadoService;
                this.auditoriaService = auditoriaService;
        }

        @Transactional
        public MovimentacaoEstoque salvar(
                        MovimentacaoEstoqueRequest request) {

                Usuario usuarioLogado =
                        obterUsuarioLogado();

                Produto produto = produtoRepository
                        .findByIdAndTenantId(
                                request.produtoId(),
                                usuarioLogado
                                        .getTenant()
                                        .getId()
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Produto não encontrado."
                                ));

                if (Boolean.FALSE.equals(
                                produto.getAtivo())) {

                        throw new RegraNegocioException(
                                "Produto inativo."
                        );
                }

                if (
                        "entrada".equals(request.tipo())
                                &&
                                (
                                        request.precoCompra() == null
                                                ||
                                                request.precoCompra()
                                                        .compareTo(BigDecimal.ZERO) <= 0
                                )
                ) {

                        throw new RegraNegocioException(
                                "Preço de compra obrigatório para entrada."
                        );

                }



                if ("entrada".equals(request.tipo())) {

                        Integer estoqueAtual = produto.getEstoqueAtual();

                        BigDecimal custoMedioAtual = produto.getCustoMedio() != null
                                        ? produto.getCustoMedio()
                                        : BigDecimal.ZERO;

                        Integer quantidadeEntrada = request.quantidade();

                        BigDecimal precoCompra = request.precoCompra();

                        if (estoqueAtual == 0) {

                                produto.setCustoMedio(
                                                precoCompra);

                        } else {

                                BigDecimal valorEstoqueAtual = custoMedioAtual.multiply(
                                                BigDecimal.valueOf(
                                                                estoqueAtual));

                                BigDecimal valorEntrada = precoCompra.multiply(
                                                BigDecimal.valueOf(
                                                                quantidadeEntrada));

                                BigDecimal novoCustoMedio = valorEstoqueAtual
                                                .add(valorEntrada)
                                                .divide(
                                                                BigDecimal.valueOf(
                                                                                estoqueAtual
                                                                                                + quantidadeEntrada),
                                                                2,
                                                                RoundingMode.HALF_UP);

                                produto.setCustoMedio(
                                                novoCustoMedio);
                        }

                        produto.setEstoqueAtual(
                                        estoqueAtual
                                                        + quantidadeEntrada);

                        produto.setDataAtualizacao(
                                        LocalDateTime.now());

                        produtoRepository.save(
                                        produto);

                                        
                } else if ("saida".equals(request.tipo())) {

                        if (produto.getEstoqueAtual() < request.quantidade()) {

                                throw new RegraNegocioException(
                                        "Estoque insuficiente."
                                );

                        }

                        produto.setEstoqueAtual(
                                produto.getEstoqueAtual()
                                        - request.quantidade()
                        );

                        produto.setDataAtualizacao(
                                LocalDateTime.now()
                        );

                        produtoRepository.save(
                                produto
                        );

                }

                else if ("ajuste".equals(request.tipo())) {

                        produto.setEstoqueAtual(
                                request.quantidade()
                        );

                        produto.setDataAtualizacao(
                                LocalDateTime.now()
                        );

                        produtoRepository.save(
                                produto
                        );

                }


                MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

                movimentacao.setProduto(
                                produto);

                movimentacao.setProdutoNome(
                                produto.getNome());

                movimentacao.setTipo(
                                request.tipo());

                movimentacao.setQuantidade(
                                request.quantidade());

                movimentacao.setPrecoCompra(
                                request.precoCompra());

                movimentacao.setObservacao(
                                request.observacao());

                movimentacao.setUsuario(
                                usuarioLogado);

                movimentacao.setTenant(
                                usuarioLogado.getTenant());

                movimentacao.setDataMovimentacao(
                                LocalDateTime.now());

                MovimentacaoEstoque movimentacaoSalva =
                        repository.save(
                                movimentacao);

                if ("entrada".equals(request.tipo())) {

                        auditoriaService.registrar(
                                usuarioLogado.getTenant().getId(),
                                usuarioLogado.getId(),
                                usuarioLogado.getNome(),
                                "ENTRADA_ESTOQUE",
                                "Produto",
                                produto.getId().toString(),
                                "Entrada de "
                                        + request.quantidade()
                                        + " unidade(s) no produto "
                                        + produto.getNome()
                        );

                } else if ("saida".equals(request.tipo())) {

                        auditoriaService.registrar(
                                usuarioLogado.getTenant().getId(),
                                usuarioLogado.getId(),
                                usuarioLogado.getNome(),
                                "SAIDA_ESTOQUE",
                                "Produto",
                                produto.getId().toString(),
                                "Saída manual de "
                                        + request.quantidade()
                                        + " unidade(s) do produto "
                                        + produto.getNome()
                        );

                } else if ("ajuste".equals(request.tipo())) {

                        auditoriaService.registrar(
                                usuarioLogado.getTenant().getId(),
                                usuarioLogado.getId(),
                                usuarioLogado.getNome(),
                                "AJUSTE_ESTOQUE",
                                "Produto",
                                produto.getId().toString(),
                                "Estoque ajustado para "
                                        + request.quantidade()
                                        + " unidade(s) no produto "
                                        + produto.getNome()
                        );

                }

                return movimentacaoSalva;
        }

        public List<MovimentacaoEstoqueResponse> listar() {

                Usuario usuarioLogado =
                        obterUsuarioLogado();

                return repository.findByTenantId(
                                usuarioLogado
                                                .getTenant()
                                                .getId())
                                .stream()
                                .map(movimentacao -> new MovimentacaoEstoqueResponse(

                                                movimentacao.getId(),

                                                movimentacao.getProdutoNome(),

                                                movimentacao.getTipo(),

                                                movimentacao.getQuantidade(),

                                                movimentacao.getPrecoCompra(),

                                                movimentacao.getObservacao(),

                                                movimentacao.getUsuario()
                                                                .getNome(),

                                                movimentacao.getDataMovimentacao()

                                ))
                                .toList();

        }

        private Usuario obterUsuarioLogado() {

                Usuario usuarioLogado =
                        usuarioAutenticadoService
                                .usuarioLogado();

                if (usuarioLogado == null) {

                        throw new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Usuário não autenticado."
                        );

                }

                return usuarioLogado;

        }

}