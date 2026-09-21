package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.request.VendaRequest;
import br.com.stockflow.stockflow_api.dto.response.VendaResponse;

import br.com.stockflow.stockflow_api.entity.*;

import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;
import br.com.stockflow.stockflow_api.repository.*;

import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.com.stockflow.stockflow_api.dto.request.ItemVendaRequest;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import br.com.stockflow.stockflow_api.dto.request.CancelarVendaRequest;

import java.util.UUID;

import br.com.stockflow.stockflow_api.dto.response.VendaDetalhesResponse;
import br.com.stockflow.stockflow_api.dto.response.ItemVendaResponse;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;

    private final ItemVendaRepository itemVendaRepository;

    private final ProdutoRepository produtoRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    private final FiadoRepository fiadoRepository;

    private final ClienteRepository clienteRepository;

    private final PromocaoRepository promocaoRepository;

    private final PromocaoService promocaoService;

    public VendaService(
            VendaRepository vendaRepository,
            ItemVendaRepository itemVendaRepository,
            ProdutoRepository produtoRepository,
            MovimentacaoEstoqueRepository movimentacaoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            FiadoRepository fiadoRepository,
            ClienteRepository clienteRepository,
            PromocaoRepository promocaoRepository,
            PromocaoService promocaoService) {


        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.movimentacaoRepository = movimentacaoRepository;
        this.fiadoRepository = fiadoRepository;
        this.clienteRepository = clienteRepository;
        this.promocaoRepository = promocaoRepository;
        this.promocaoService = promocaoService;

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

        if (request.itens() == null
                || request.itens().isEmpty()) {

            throw new RegraNegocioException(
                    "Carrinho vazio."
            );
        }

        if (request.formaPagamento() == null
                || request.formaPagamento().isBlank()) {

            throw new RegraNegocioException(
                    "Forma de pagamento obrigatória."
            );
        }

        if ("fiado".equals(
                request.formaPagamento())
                && request.clienteId() == null) {

            throw new RegraNegocioException(
                    "Selecione um cliente para venda fiada."
            );
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        Integer quantidadeItens = 0;

        for (ItemVendaRequest item : request.itens()) {

            Produto produto = produtoRepository
                    .findById(
                            item.produtoId())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Produto não encontrado."
                            ));

            if (Boolean.FALSE.equals(
                    produto.getAtivo())) {

                throw new RegraNegocioException(
                        "Produto inativo: "
                                + produto.getNome()
                );
            }

            if (item.quantidade() == null
                    || item.quantidade() <= 0) {

                throw new RegraNegocioException(
                        "Quantidade inválida."
                );
            }

            if (produto.getEstoqueAtual() < item.quantidade()) {

                throw new RegraNegocioException(
                        "Estoque insuficiente para o produto: "
                                + produto.getNome()
                );
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
                            item.quantidade()));

            valorTotal = valorTotal.add(subtotal);

            quantidadeItens += item.quantidade();
        }
        Venda venda = new Venda();

        venda.setDataVenda(
                LocalDateTime.now());

        venda.setFormaPagamento(
                request.formaPagamento());

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
                request.clienteId());

        Venda vendaSalva = vendaRepository.save(
                venda);
        if ("fiado".equalsIgnoreCase(
                request.formaPagamento())) {

            Cliente cliente =
                    clienteRepository
                            .findById(
                                    request.clienteId())
                            .orElseThrow(
                                    () ->
                                            new RecursoNaoEncontradoException(
                                                    "Cliente não encontrado."
                                            ));

            Fiado fiado = new Fiado();

            fiado.setCliente(
                    cliente);

            fiado.setVendaId(
                    vendaSalva.getId());

            fiado.setValorTotal(
                    valorTotal);

            fiado.setDataLancamento(
                    LocalDateTime.now());

            fiado.setStatus(
                    "pendente");

            fiado.setTenant(
                    usuarioLogado.getTenant());

            fiadoRepository.save(
                    fiado);
        }

        for (ItemVendaRequest item : request.itens()) {

            Produto produto = produtoRepository
                    .findById(
                            item.produtoId())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Produto não encontrado."
                            ));

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
                    item.quantidade());

            itemVenda.setValorUnitario(
                    precoAplicado);

            itemVenda.setSubtotal(
                    precoAplicado.multiply(
                            BigDecimal.valueOf(
                                    item.quantidade())));

            itemVenda.setPromocaoAplicada(
                    Boolean.TRUE.equals(
                            produto.getPromocaoAtiva()));

            itemVendaRepository.save(
                    itemVenda);

            if (Boolean.TRUE.equals(
                    produto.getPromocaoAtiva())) {
                System.out.println(
                        "PROMOCAO ATIVA? "
                                + produto.getPromocaoAtiva()
                );
                System.out.println(
                        "PRODUTO EM PROMOCAO: "
                                + produto.getNome()
                );

                promocaoRepository
                        .findByProdutoAndAtivaTrue(
                                produto
                        );
                var promocaoOpt =
                        promocaoRepository
                                .findByProdutoAndAtivaTrue(
                                        produto
                                );


                promocaoOpt.ifPresent(promocao -> {

                            Integer vendidas =
                                    promocao.getUnidadesVendidas();

                            if (vendidas == null) {
                                vendidas = 0;
                            }

                            promocao.setUnidadesVendidas(
                                    vendidas +
                                            item.quantidade()
                            );

                            BigDecimal receitaAtual =
                                    promocao.getReceitaGerada();

                            if (receitaAtual == null) {
                                receitaAtual =
                                        BigDecimal.ZERO;
                            }

                            BigDecimal receitaVenda =
                                    precoAplicado.multiply(
                                            BigDecimal.valueOf(
                                                    item.quantidade()
                                            )
                                    );

                            promocao.setReceitaGerada(
                                    receitaAtual.add(
                                            receitaVenda
                                    )
                            );

                    promocaoRepository.save(promocao);

                    if (promocao.getMetaUnidades() != null
                            && promocao.getUnidadesVendidas()
                            >= promocao.getMetaUnidades()) {

                        promocaoService.finalizarMetaAtingida(
                                promocao,
                                produto
                        );
                    }

                        });

            }

            produto.setEstoqueAtual(
                    produto.getEstoqueAtual()
                            - item.quantidade());

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
                    item.quantidade());

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

        if (request.motivo() == null
                || request.motivo().isBlank()) {

            throw new RegraNegocioException(
                    "Motivo do cancelamento é obrigatório."
            );
        }

        Venda venda = vendaRepository
                .findById(vendaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Venda não encontrada."
                        ));

        if ("cancelada".equals(
                venda.getStatus())) {

            throw new RegraNegocioException(
                    "Venda já cancelada."
            );

        }

        if (venda.getDataVenda()
                .isBefore(
                        LocalDateTime.now()
                                .minusHours(24))) {

            throw new RegraNegocioException(
                    "Esta venda não pode mais ser cancelada. Prazo máximo excedido."
            );
        }

        venda.setStatus(
                "cancelada");

        venda.setMotivoCancelamento(
                request.motivo());

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
                                new RecursoNaoEncontradoException(
                                        "Venda não encontrada."
                                ));

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