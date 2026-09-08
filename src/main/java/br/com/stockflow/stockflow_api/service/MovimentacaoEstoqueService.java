package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.MovimentacaoEstoque;
import br.com.stockflow.stockflow_api.repository.MovimentacaoEstoqueRepository;

import org.springframework.stereotype.Service;

import java.util.List;

import br.com.stockflow.stockflow_api.dto.MovimentacaoEstoqueRequest;

import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.entity.Usuario;

import br.com.stockflow.stockflow_api.repository.ProdutoRepository;

import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.com.stockflow.stockflow_api.dto.MovimentacaoEstoqueResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MovimentacaoEstoqueService {

        private final MovimentacaoEstoqueRepository repository;

        private final ProdutoRepository produtoRepository;

        private final UsuarioAutenticadoService usuarioAutenticadoService;

        public MovimentacaoEstoqueService(
                        MovimentacaoEstoqueRepository repository,
                        ProdutoRepository produtoRepository,
                        UsuarioAutenticadoService usuarioAutenticadoService) {

                this.repository = repository;

                this.produtoRepository = produtoRepository;

                this.usuarioAutenticadoService = usuarioAutenticadoService;

        }

        @Transactional
        public MovimentacaoEstoque salvar(
                        MovimentacaoEstoqueRequest request) {
                Produto produto = produtoRepository
                                .findById(
                                                request.getProdutoId())
                                .orElseThrow();

                if (request.getQuantidade() == null
                                || request.getQuantidade() <= 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Quantidade inválida.");
                }

                if (request.getPrecoCompra() == null
                                || request.getPrecoCompra()
                                                .compareTo(BigDecimal.ZERO) <= 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Preço de compra inválido.");
                }

                if (Boolean.FALSE.equals(
                                produto.getAtivo())) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Produto inativo.");
                }

                if ("entrada".equals(request.getTipo())) {

                        Integer estoqueAtual = produto.getEstoqueAtual();

                        BigDecimal custoMedioAtual = produto.getCustoMedio() != null
                                        ? produto.getCustoMedio()
                                        : BigDecimal.ZERO;

                        Integer quantidadeEntrada = request.getQuantidade();

                        BigDecimal precoCompra = request.getPrecoCompra();

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
                                        
                } else if ("saida".equals(request.getTipo())) {

                        if (produto.getEstoqueAtual() < request.getQuantidade()) {

                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Estoque insuficiente.");
                        }

                }

                Usuario usuarioLogado = usuarioAutenticadoService
                                .usuarioLogado();

                if (usuarioLogado == null) {

                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Usuário não autenticado");

                }

                MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

                movimentacao.setProduto(
                                produto);

                movimentacao.setProdutoNome(
                                produto.getNome());

                movimentacao.setTipo(
                                request.getTipo());

                movimentacao.setQuantidade(
                                request.getQuantidade());

                movimentacao.setPrecoCompra(
                                request.getPrecoCompra());

                movimentacao.setObservacao(
                                request.getObservacao());

                movimentacao.setUsuario(
                                usuarioLogado);

                movimentacao.setTenant(
                                usuarioLogado.getTenant());

                movimentacao.setDataMovimentacao(
                                LocalDateTime.now());

                return repository.save(
                                movimentacao);

        }

        public List<MovimentacaoEstoqueResponse> listar() {

                Usuario usuarioLogado = usuarioAutenticadoService
                                .usuarioLogado();

                if (usuarioLogado == null) {

                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Usuário não autenticado");

                }

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

}