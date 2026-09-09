package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.VendaRequest;
import br.com.stockflow.stockflow_api.dto.VendaResponse;

import br.com.stockflow.stockflow_api.entity.Venda;

import br.com.stockflow.stockflow_api.repository.VendaRepository;
import br.com.stockflow.stockflow_api.repository.ItemVendaRepository;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;

import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.stereotype.Service;

import java.util.List;

import br.com.stockflow.stockflow_api.entity.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.com.stockflow.stockflow_api.dto.ItemVendaRequest;
import br.com.stockflow.stockflow_api.entity.Produto;

import java.math.BigDecimal;

import br.com.stockflow.stockflow_api.entity.Venda;

import java.time.LocalDateTime;

import br.com.stockflow.stockflow_api.entity.ItemVenda;
import br.com.stockflow.stockflow_api.repository.MovimentacaoEstoqueRepository;
import br.com.stockflow.stockflow_api.entity.MovimentacaoEstoque;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.stockflow.stockflow_api.dto.CancelarVendaRequest;
import br.com.stockflow.stockflow_api.entity.ItemVenda;

import java.util.UUID;

import br.com.stockflow.stockflow_api.dto.VendaDetalhesResponse;
import br.com.stockflow.stockflow_api.dto.ItemVendaResponse;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;

    private final ItemVendaRepository itemVendaRepository;

    private final ProdutoRepository produtoRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public VendaService(
            VendaRepository vendaRepository,
            ItemVendaRepository itemVendaRepository,
            ProdutoRepository produtoRepository,
            MovimentacaoEstoqueRepository movimentacaoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public Venda salvar(
            VendaRequest request) {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        if (request.getItens() == null
                || request.getItens().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Carrinho vazio.");
        }

        if (request.getFormaPagamento() == null
                || request.getFormaPagamento().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Forma de pagamento obrigatória.");
        }

        if ("fiado".equals(
                request.getFormaPagamento())
                && request.getClienteId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione um cliente para venda fiada.");
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        Integer quantidadeItens = 0;

        for (ItemVendaRequest item : request.getItens()) {

            Produto produto = produtoRepository
                    .findById(
                            item.getProdutoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Produto não encontrado."));

            if (Boolean.FALSE.equals(
                    produto.getAtivo())) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Produto inativo: "
                                + produto.getNome());
            }

            if (item.getQuantidade() == null
                    || item.getQuantidade() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Quantidade inválida.");
            }

            if (produto.getEstoqueAtual() < item.getQuantidade()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estoque insuficiente para o produto: "
                                + produto.getNome());
            }

            BigDecimal precoAplicado;

            if (Boolean.TRUE.equals(
                    produto.getPromocaoAtiva())
                    && produto.getPrecoPromocional() != null) {

                precoAplicado = produto.getPrecoPromocional();

            } else {

                precoAplicado = produto.getPrecoVenda();
            }

            BigDecimal subtotal = precoAplicado.multiply(
                    BigDecimal.valueOf(
                            item.getQuantidade()));

            valorTotal = valorTotal.add(subtotal);

            quantidadeItens += item.getQuantidade();
        }
        Venda venda = new Venda();

        venda.setDataVenda(
                LocalDateTime.now());

        venda.setFormaPagamento(
                request.getFormaPagamento());

        venda.setValorTotal(
                valorTotal);

        venda.setQuantidadeItens(
                quantidadeItens);

        venda.setStatus(
                "finalizada");

        venda.setUsuario(
                usuarioLogado);

        venda.setTenant(
                usuarioLogado.getTenant());

        venda.setClienteId(
                request.getClienteId());

        Venda vendaSalva = vendaRepository.save(
                venda);

        for (ItemVendaRequest item : request.getItens()) {

            Produto produto = produtoRepository
                    .findById(
                            item.getProdutoId())
                    .orElseThrow();

            BigDecimal precoAplicado;

            if (Boolean.TRUE.equals(
                    produto.getPromocaoAtiva())
                    && produto.getPrecoPromocional() != null) {

                precoAplicado = produto.getPrecoPromocional();

            } else {

                precoAplicado = produto.getPrecoVenda();
            }

            ItemVenda itemVenda = new ItemVenda();

            itemVenda.setVenda(
                    vendaSalva);

            itemVenda.setProduto(
                    produto);

            itemVenda.setProdutoNome(
                    produto.getNome());

            itemVenda.setQuantidade(
                    item.getQuantidade());

            itemVenda.setValorUnitario(
                    precoAplicado);

            itemVenda.setSubtotal(
                    precoAplicado.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantidade())));

            itemVenda.setPromocaoAplicada(
                    Boolean.TRUE.equals(
                            produto.getPromocaoAtiva()));

            itemVendaRepository.save(
                    itemVenda);

            produto.setEstoqueAtual(
                    produto.getEstoqueAtual()
                            - item.getQuantidade());

            produto.setDataAtualizacao(
                    LocalDateTime.now());

            produtoRepository.save(
                    produto);
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

            movimentacao.setProduto(
                    produto);

            movimentacao.setProdutoNome(
                    produto.getNome());

            movimentacao.setTipo(
                    "saida");

            movimentacao.setQuantidade(
                    item.getQuantidade());

            movimentacao.setPrecoCompra(
                    BigDecimal.ZERO);

            movimentacao.setUsuario(
                    usuarioLogado);

            movimentacao.setTenant(
                    usuarioLogado.getTenant());

            movimentacao.setDataMovimentacao(
                    LocalDateTime.now());

            movimentacaoRepository.save(
                    movimentacao);

        }

        return vendaSalva;
    }

    @Transactional
    public void cancelarVenda(
            UUID vendaId,
            CancelarVendaRequest request) {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        if (request.getMotivo() == null
                || request.getMotivo().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Motivo do cancelamento é obrigatório.");
        }

        Venda venda = vendaRepository
                .findById(vendaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Venda não encontrada."));

        if ("cancelada".equals(
                venda.getStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Venda já cancelada.");
        }

        if (venda.getDataVenda()
                .isBefore(
                        LocalDateTime.now()
                                .minusHours(24))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Esta venda não pode mais ser cancelada. Prazo máximo excedido.");
        }

        venda.setStatus(
                "cancelada");

        venda.setMotivoCancelamento(
                request.getMotivo());

        venda.setDataCancelamento(
                LocalDateTime.now());

        venda.setUsuarioCancelamento(
                usuarioLogado);

        List<ItemVenda> itensVenda = itemVendaRepository
                .findByVendaId(
                        venda.getId());
        for (ItemVenda item : itensVenda) {

            Produto produto = item.getProduto();

            produto.setEstoqueAtual(
                    produto.getEstoqueAtual()
                            + item.getQuantidade());

            produto.setDataAtualizacao(
                    LocalDateTime.now());

            produtoRepository.save(
                    produto);

            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

            movimentacao.setProduto(
                    produto);

            movimentacao.setProdutoNome(
                    produto.getNome());

            movimentacao.setTipo(
                    "ajuste");

            movimentacao.setQuantidade(
                    item.getQuantidade());

            movimentacao.setPrecoCompra(
                    BigDecimal.ZERO);

            movimentacao.setObservacao(
                    "Estorno de venda cancelada");

            movimentacao.setUsuario(
                    usuarioLogado);

            movimentacao.setTenant(
                    usuarioLogado.getTenant());

            movimentacao.setDataMovimentacao(
                    LocalDateTime.now());

            movimentacaoRepository.save(
                    movimentacao);
        }
        vendaRepository.save(
                venda);
    }

    public List<VendaResponse> listar() {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        return vendaRepository
                .findByTenantId(
                        usuarioLogado
                                .getTenant()
                                .getId())
                .stream()
                .map(venda -> new VendaResponse(
                        venda.getId(),
                        venda.getDataVenda(),
                        venda.getFormaPagamento(),
                        venda.getValorTotal(),
                        venda.getQuantidadeItens(),
                        venda.getClienteNome(),
                        venda.getStatus(),
                        venda.getUsuario().getNome()))
                .toList();
    }

    public VendaDetalhesResponse buscarPorId(
            UUID vendaId) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        Venda venda =
                vendaRepository
                        .findById(vendaId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Venda não encontrada."));

        List<ItemVendaResponse> itens =
                itemVendaRepository
                        .findByVendaId(vendaId)
                        .stream()
                        .map(item ->
                                new ItemVendaResponse(
                                        item.getProduto().getId(),
                                        item.getProdutoNome(),
                                        item.getQuantidade(),
                                        item.getSubtotal(),
                                        item.getPromocaoAplicada()))
                        .toList();

        return new VendaDetalhesResponse(
                venda.getId(),
                venda.getDataVenda(),
                venda.getFormaPagamento(),
                venda.getValorTotal(),
                venda.getQuantidadeItens(),
                venda.getClienteNome(),
                venda.getStatus(),
                venda.getMotivoCancelamento(),
                venda.getDataCancelamento(),
                venda.getUsuarioCancelamento() != null
                        ? venda.getUsuarioCancelamento().getNome()
                        : null,

                venda.getUsuario() != null
                        ? venda.getUsuario().getNome()
                        : null,

                itens);
    }
}