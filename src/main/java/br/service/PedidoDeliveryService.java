package br.service;

import br.dto.request.CriarPedidoDeliveryRequest;
import br.dto.request.VendaDeliveryItemRequest;
import br.dto.request.VendaDeliveryRequest;
import br.dto.response.*;
import br.entity.*;
import br.exception.RecursoNaoEncontradoException;
import br.exception.RegraNegocioException;
import br.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import br.dto.request.CriarPedidoDeliveryItemRequest;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import br.service.VendaService;

@Service
@RequiredArgsConstructor
public class PedidoDeliveryService {

    private final PedidoDeliveryRepository pedidoDeliveryRepository;
    private final PedidoDeliveryItemRepository pedidoDeliveryItemRepository;
    private final ProdutoRepository produtoRepository;
    private final TenantRepository tenantRepository;
    private final VendaService vendaService;

    public PedidoDeliveryResponse criarPedido(
            String slugTenant,
            CriarPedidoDeliveryRequest request
    ) {

        Tenant tenant = tenantRepository
                .findBySlug(slugTenant)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        )
                );

        if (
                !Boolean.TRUE.equals(
                        tenant.getDeliveryHabilitado()
                )
        ) {

            throw new RegraNegocioException(
                    "Delivery indisponível para este estabelecimento."
            );

        }

        if (request.itens() == null
                || request.itens().isEmpty()) {

            throw new RegraNegocioException(
                    "O pedido deve possuir pelo menos um item."
            );

        }

        Long sequencial = pedidoDeliveryRepository
                .findTopByTenantIdAndSequencialIsNotNullOrderBySequencialDesc(
                        tenant.getId()
                )
                .map(PedidoDelivery::getSequencial)
                .orElse(0L) + 1;

        String numeroPedido = String.format(
                "%s-%06d",
                tenant.getCodigoTenant(),
                sequencial
        );

        PedidoDelivery pedido = new PedidoDelivery();

        pedido.setSequencial(sequencial);

        pedido.setNumero(numeroPedido);

        pedido.setClienteNome(request.clienteNome());

        pedido.setTelefone(request.telefone());

        pedido.setEndereco(request.endereco());

        pedido.setComplemento(request.complemento());

        pedido.setObservacao(request.observacao());

        pedido.setFormaPagamento(
                request.formaPagamento()
        );

        pedido.setStatus(StatusPedidoDelivery.NOVO);

        pedido.setDataPedido(LocalDateTime.now());

        pedido.setTenant(tenant);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CriarPedidoDeliveryItemRequest itemRequest : request.itens()) {

            Produto produto = produtoRepository.findByIdAndTenantId(
                            itemRequest.produtoId(),
                            tenant.getId()
                    )
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Produto não encontrado."
                            )
                    );

            if (itemRequest.quantidade() == null
                    || itemRequest.quantidade() <= 0) {

                throw new RegraNegocioException(
                        "Quantidade inválida."
                );

            }

            if (!produto.getAtivo()) {

                throw new RegraNegocioException(
                        "Produto inativo."
                );

            }

            if (produto.getEstoqueAtual()
                    < itemRequest.quantidade()) {

                throw new RegraNegocioException(
                        "Estoque insuficiente para o produto: "
                                + produto.getNome()
                );

            }

            BigDecimal precoAplicado;

            if (Boolean.TRUE.equals(produto.getPromocaoAtiva())
                    && produto.getPrecoPromocional() != null) {

                precoAplicado = produto.getPrecoPromocional();

            } else {

                precoAplicado = produto.getPrecoVenda();

            }

            BigDecimal subtotal =
                    precoAplicado.multiply(
                            BigDecimal.valueOf(
                                    itemRequest.quantidade()
                            )
                    );

            valorTotal = valorTotal.add(subtotal);

        }

        pedido.setValorTotal(valorTotal);

        pedido = pedidoDeliveryRepository.save(pedido);

        for (CriarPedidoDeliveryItemRequest itemRequest : request.itens()) {

            Produto produto = produtoRepository.findByIdAndTenantId(
                            itemRequest.produtoId(),
                            tenant.getId()
                    )
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Produto não encontrado."
                            )
                    );

            BigDecimal precoAplicado;

            if (Boolean.TRUE.equals(produto.getPromocaoAtiva())
                    && produto.getPrecoPromocional() != null) {

                precoAplicado = produto.getPrecoPromocional();

            } else {

                precoAplicado = produto.getPrecoVenda();

            }

            BigDecimal subtotal =
                    precoAplicado.multiply(
                            BigDecimal.valueOf(
                                    itemRequest.quantidade()
                            )
                    );

            PedidoDeliveryItem item = new PedidoDeliveryItem();

            item.setPrecoUnitario(
                    precoAplicado
            );

            item.setSubtotal(
                    subtotal
            );

            item.setPedido(pedido);

            item.setProduto(produto);

            item.setTenant(tenant);

            item.setQuantidade(
                    itemRequest.quantidade()
            );

            pedidoDeliveryItemRepository.save(item);

        }

        return converterParaResponse(
                pedido
        );

    }

    public List<CardapioProdutoResponse> listarCardapio(
            String slugTenant
    ) {

        Tenant tenant = tenantRepository
                .findBySlug(slugTenant)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        )
                );

        if (
                !Boolean.TRUE.equals(
                        tenant.getDeliveryHabilitado()
                )
        ) {

            throw new RegraNegocioException(
                    "Delivery indisponível para este estabelecimento."
            );

        }

        List<Produto> produtos =
                produtoRepository
                        .findByTenantIdAndAtivoTrueAndEstoqueAtualGreaterThanOrderByNomeAsc(
                                tenant.getId(),
                                0
                        );

        if (produtos == null) {

            return List.of();

        }

        return produtos.stream()
                .map(produto ->
                        new CardapioProdutoResponse(
                                produto.getId(),
                                produto.getNome(),
                                produto.getCategoria(),
                                produto.getPrecoVenda(),
                                produto.getEstoqueAtual(),
                                null
                        )
                )
                .toList();

    }

    public List<PedidoDeliveryResumoResponse>
    listarPedidos(
            String slugTenant,
            StatusPedidoDelivery status,
            String periodo
    )
    {

        Tenant tenant = tenantRepository
                .findBySlug(slugTenant)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        )
                );

        LocalDateTime dataInicial =
                obterDataInicial(periodo);

        List<PedidoDelivery> pedidos;

        if (status != null) {

            pedidos =
                    pedidoDeliveryRepository
                            .findByTenantIdAndStatusAndDataPedidoGreaterThanEqualOrderByDataPedidoDesc(
                                    tenant.getId(),
                                    status,
                                    dataInicial
                            );

        } else {

            pedidos =
                    pedidoDeliveryRepository
                            .findByTenantIdAndDataPedidoGreaterThanEqualOrderByDataPedidoDesc(
                                    tenant.getId(),
                                    dataInicial
                            );

        }


        return pedidos.stream()
                .map(pedido -> {

                    Integer quantidadeProdutos =
                            pedidoDeliveryItemRepository
                                    .findByPedidoId(
                                            pedido.getId()
                                    )
                                    .stream()
                                    .mapToInt(
                                            PedidoDeliveryItem::getQuantidade
                                    )
                                    .sum();

                    boolean atrasado =
                            (pedido.getStatus() == StatusPedidoDelivery.NOVO
                                    || pedido.getStatus() == StatusPedidoDelivery.SEPARANDO
                                    || pedido.getStatus() == StatusPedidoDelivery.PRONTO_PARA_ENTREGA)
                                    &&
                                    pedido.getDataPedido().isBefore(
                                            LocalDateTime.now()
                                                    .minusMinutes(30)
                                    );

                    return new PedidoDeliveryResumoResponse(
                            pedido.getId(),
                            pedido.getNumero(),
                            pedido.getClienteNome(),
                            pedido.getTelefone(),
                            pedido.getFormaPagamento(),
                            quantidadeProdutos,
                            pedido.getValorTotal(),
                            pedido.getStatus(),
                            pedido.getDataPedido(),
                            pedido.getDataPedido()
                                    .toLocalTime()
                                    .withSecond(0)
                                    .withNano(0)
                                    .toString(),
                            atrasado

                    );

                })
                .toList();
    }

    public PedidoDeliveryResponse atualizarStatus(
            String slugTenant,
            UUID pedidoId,
            StatusPedidoDelivery novoStatus
    ) {

        Tenant tenant = tenantRepository
                .findBySlug(slugTenant)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        )
                );

        PedidoDelivery pedido =
                pedidoDeliveryRepository
                        .findByIdAndTenantId(
                                pedidoId,
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Pedido não encontrado."
                                )
                        );

        StatusPedidoDelivery statusAtual =
                pedido.getStatus();

        validarTransicaoStatus(
                statusAtual,
                novoStatus
        );

        pedido.setStatus(novoStatus);

        if (novoStatus == StatusPedidoDelivery.EM_ENTREGA) {

            pedido.setHoraSaidaEntrega(
                    LocalTime.now()
            );

        }

        if (novoStatus == StatusPedidoDelivery.ENTREGUE) {

            pedido.setDataEntrega(
                    LocalDateTime.now()
            );

            gerarVendaDelivery(
                    pedido
            );

        }

        PedidoDelivery pedidoAtualizado =
                pedidoDeliveryRepository.save(
                        pedido
                );

        return converterParaResponse(
                pedidoAtualizado
        );
    }

    private void validarTransicaoStatus(
            StatusPedidoDelivery atual,
            StatusPedidoDelivery novo
    ) {

        boolean valido =
                switch (atual) {

                    case NOVO -> novo == StatusPedidoDelivery.SEPARANDO
                            || novo == StatusPedidoDelivery.CANCELADO;

                    case SEPARANDO -> novo == StatusPedidoDelivery.PRONTO_PARA_ENTREGA
                            || novo == StatusPedidoDelivery.CANCELADO;

                    case PRONTO_PARA_ENTREGA -> novo == StatusPedidoDelivery.EM_ENTREGA
                            || novo == StatusPedidoDelivery.CANCELADO;

                    case EM_ENTREGA -> novo == StatusPedidoDelivery.ENTREGUE
                            || novo == StatusPedidoDelivery.CANCELADO;

                    case ENTREGUE, CANCELADO -> false;
                };

        if (!valido) {

            throw new RegraNegocioException(
                    "Transição de status inválida."
            );

        }
    }

    public PedidoDeliveryDetalheResponse buscarPedido(
            String slugTenant,
            UUID pedidoId
    ) {

    Tenant tenant = tenantRepository
            .findBySlug(slugTenant)
            .orElseThrow(() ->
                    new RecursoNaoEncontradoException(
                            "Tenant não encontrado."
                    )
            );

    PedidoDelivery pedido =
            pedidoDeliveryRepository
                    .findByIdAndTenantId(
                            pedidoId,
                            tenant.getId()
                    )
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Pedido não encontrado."
                            )
                    );

    List<PedidoDeliveryItem> itens =
            pedidoDeliveryItemRepository
                    .findByPedidoId(
                            pedido.getId()
                    );

    List<PedidoDeliveryItemResponse> itensResponse =
            itens.stream()
                    .map(item ->
                            new PedidoDeliveryItemResponse(
                                    item.getProduto().getNome(),
                                    item.getQuantidade(),
                                    item.getPrecoUnitario(),
                                    item.getSubtotal()
                            )
                    )
                    .toList();

        Long tempoEntregaMinutos = null;

        if (pedido.getHoraSaidaEntrega() != null
                && pedido.getDataEntrega() != null) {

            tempoEntregaMinutos =
                    java.time.Duration.between(
                                    pedido.getHoraSaidaEntrega(),
                                    pedido.getDataEntrega()
                                            .toLocalTime()
                            )
                            .toMinutes();

        }

        return new PedidoDeliveryDetalheResponse(
                pedido.getId(),
                pedido.getNumero(),
                pedido.getClienteNome(),
                pedido.getTelefone(),
                pedido.getFormaPagamento(),
                pedido.getEndereco(),
                pedido.getComplemento(),
                pedido.getObservacao(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataPedido(),
                pedido.getDataEntrega(),
                pedido.getHoraSaidaEntrega(),
                tempoEntregaMinutos,
                pedido.getVendaId(),
                itensResponse
        );
}

    private PedidoDeliveryResponse converterParaResponse(
            PedidoDelivery pedido
    ) {

        return new PedidoDeliveryResponse(
                pedido.getId(),
                pedido.getNumero(),
                pedido.getSequencial(),
                pedido.getClienteNome(),
                pedido.getTelefone(),
                pedido.getFormaPagamento(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataPedido(),
                pedido.getDataEntrega(),
                pedido.getDataPedido()
                        .toLocalTime()
                        .withSecond(0)
                        .withNano(0)
                        .toString()
        );

    }

    public PedidoDeliveryResumoDashboardResponse obterResumo(
            String slugTenant,
            String periodo
    ){
        Tenant tenant = tenantRepository
                .findBySlug(slugTenant)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        )
                );

        LocalDateTime dataInicial = obterDataInicial(periodo);

        Long novos =
                pedidoDeliveryRepository
                        .countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(
                                tenant.getId(),
                                StatusPedidoDelivery.NOVO,
                                dataInicial
                        );

        Long separando =
                pedidoDeliveryRepository
                        .countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(
                                tenant.getId(),
                                StatusPedidoDelivery.SEPARANDO,
                                dataInicial
                        );

        Long prontosParaEntrega =
                pedidoDeliveryRepository
                        .countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(
                                tenant.getId(),
                                StatusPedidoDelivery.PRONTO_PARA_ENTREGA,
                                dataInicial
                        );

        Long emEntrega =
                pedidoDeliveryRepository
                        .countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(
                                tenant.getId(),
                                StatusPedidoDelivery.EM_ENTREGA,
                                dataInicial
                        );

        Long entregues =
                pedidoDeliveryRepository
                        .countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(
                                tenant.getId(),
                                StatusPedidoDelivery.ENTREGUE,
                                dataInicial
                        );

        Long cancelados =
                pedidoDeliveryRepository
                        .countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(
                                tenant.getId(),
                                StatusPedidoDelivery.CANCELADO,
                                dataInicial
                        );

        BigDecimal valorNovos =
                somarValorPedidos(
                        tenant.getId(),
                        StatusPedidoDelivery.NOVO,
                        dataInicial

                );

        BigDecimal valorSeparando =
                somarValorPedidos(
                        tenant.getId(),
                        StatusPedidoDelivery.SEPARANDO,
                        dataInicial

                );

        BigDecimal valorProntosParaEntrega =
                somarValorPedidos(
                        tenant.getId(),
                        StatusPedidoDelivery.PRONTO_PARA_ENTREGA,
                        dataInicial

                );

        BigDecimal valorEmEntrega =
                somarValorPedidos(
                        tenant.getId(),
                        StatusPedidoDelivery.EM_ENTREGA,
                        dataInicial

                );

        BigDecimal valorEntregues =
                somarValorPedidos(
                        tenant.getId(),
                        StatusPedidoDelivery.ENTREGUE,
                        dataInicial

                );

        return new PedidoDeliveryResumoDashboardResponse(
                novos,
                valorNovos,

                separando,
                valorSeparando,

                prontosParaEntrega,
                valorProntosParaEntrega,

                emEntrega,
                valorEmEntrega,

                entregues,
                valorEntregues,

                cancelados
        );
    }

    private BigDecimal somarValorPedidos(
            UUID tenantId,
            StatusPedidoDelivery status,
            LocalDateTime dataInicial
    ) {

        return pedidoDeliveryRepository
                .findByTenantIdAndStatusAndDataPedidoGreaterThanEqual(
                        tenantId,
                        status,
                        dataInicial
                )
                .stream()
                .map(PedidoDelivery::getValorTotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

    }

    private boolean vendaJaGerada(
            PedidoDelivery pedido
    ) {

        return pedido.getVendaId() != null;

    }

    private UUID gerarVendaDelivery(
            PedidoDelivery pedido
    ) {

        if (vendaJaGerada(pedido)) {

            return pedido.getVendaId();

        }

        List<PedidoDeliveryItem> itens =
                pedidoDeliveryItemRepository
                        .findByPedidoId(
                                pedido.getId()
                        );

        VendaDeliveryRequest request =
                new VendaDeliveryRequest(

                        pedido.getId(),

                        pedido.getClienteNome(),

                        pedido.getFormaPagamento(),

                        itens.stream()
                                .map(item ->
                                        new VendaDeliveryItemRequest(
                                                item.getProduto().getId(),
                                                item.getQuantidade()
                                        )
                                )
                                .toList()

                );

        UUID vendaId =
                vendaService.criarVendaDelivery(
                        request
                );

        pedido.setVendaId(
                vendaId
        );

        pedidoDeliveryRepository.save(
                pedido
        );

        return vendaId;

    }

    private LocalDateTime obterDataInicial(
            String periodo
    ) {

        LocalDateTime agora =
                LocalDateTime.now();

        return switch (periodo) {

            case "7dias" ->
                    agora.minusDays(7);

            case "30dias" ->
                    agora.minusDays(30);

            case "mes" ->
                    agora.withDayOfMonth(1)
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);

            case "todos" ->
                    LocalDateTime.of(
                            2000,
                            1,
                            1,
                            0,
                            0
                    );

            default ->
                    agora.toLocalDate()
                            .atStartOfDay();

        };

    }

    public List<FormaPagamentoDeliveryResponse>
    obterFormasPagamento(

            String slugTenant,

            String periodo

    ) {

        Tenant tenant =
                tenantRepository
                        .findBySlug(slugTenant)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Tenant não encontrado."
                                )
                        );

        LocalDateTime dataInicial = obterDataInicial(periodo);

        List<PedidoDelivery> pedidos =

                pedidoDeliveryRepository

                        .findByTenantIdAndStatusAndDataPedidoGreaterThanEqual(

                                tenant.getId(),

                                StatusPedidoDelivery.ENTREGUE,

                                dataInicial

                        );

        return pedidos.stream()

                .filter(
                        pedido ->
                                pedido.getFormaPagamento()
                                        != null
                )

                .collect(
                        java.util.stream.Collectors.groupingBy(
                                PedidoDelivery::getFormaPagamento
                        )
                )

                .entrySet()

                .stream()

                .map(entry -> {

                    String formaPagamento = entry.getKey();

                    List<PedidoDelivery> itens = entry.getValue();

                    Long quantidade = (long) itens.size();

                    BigDecimal valorTotal =
                            itens.stream()

                                    .map(
                                            PedidoDelivery::getValorTotal
                                    )

                                    .reduce(
                                            BigDecimal.ZERO,

                                            BigDecimal::add
                                    );

                    return new FormaPagamentoDeliveryResponse(

                            formaPagamento,

                            quantidade,

                            valorTotal

                    );

                })

                .toList();

    }

    public DeliveryEntregasResumoResponse
    obterResumoEntregas(

            String slugTenant,

            String periodo

    ) {
        Tenant tenant =
                tenantRepository
                        .findBySlug(
                                slugTenant
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Tenant não encontrado."
                                )
                        );

        LocalDateTime dataInicial =
                obterDataInicial(
                        periodo
                );

        List<PedidoDelivery> entregues =

                pedidoDeliveryRepository

                        .findByTenantIdAndStatusAndDataPedidoGreaterThanEqual(

                                tenant.getId(),

                                StatusPedidoDelivery.ENTREGUE,

                                dataInicial

                        );

        Long pedidosEmEntrega =

                pedidoDeliveryRepository

                        .countByTenantIdAndStatusAndDataPedidoGreaterThanEqual(

                                tenant.getId(),

                                StatusPedidoDelivery.EM_ENTREGA,

                                dataInicial

                        );

        Long pedidosAtrasados =

                pedidoDeliveryRepository

                        .findByTenantIdAndDataPedidoGreaterThanEqualOrderByDataPedidoDesc(

                                tenant.getId(),

                                dataInicial

                        )

                        .stream()

                        .filter(

                                pedido ->

                                        (
                                                pedido.getStatus()
                                                        == StatusPedidoDelivery.NOVO

                                                        ||

                                                        pedido.getStatus()
                                                                == StatusPedidoDelivery.SEPARANDO

                                                        ||

                                                        pedido.getStatus()
                                                                == StatusPedidoDelivery.PRONTO_PARA_ENTREGA
                                        )

                                                &&

                                                pedido.getDataPedido()
                                                        .isBefore(
                                                                LocalDateTime.now()
                                                                        .minusMinutes(30)
                                                        )

                        )

                        .count();

        long totalMinutos =

                entregues.stream()

                        .filter(

                                pedido ->

                                        pedido
                                                .getHoraSaidaEntrega()
                                                != null

                                                &&

                                                pedido
                                                        .getDataEntrega()
                                                        != null

                        )

                        .mapToLong(

                                pedido ->

                                        java.time.Duration

                                                .between(

                                                        pedido
                                                                .getHoraSaidaEntrega(),

                                                        pedido
                                                                .getDataEntrega()
                                                                .toLocalTime()

                                                )

                                                .toMinutes()

                        )

                        .sum();

        Long tempoMedioEntrega =

                entregues.isEmpty()

                        ? 0L

                        : totalMinutos
                          /
                        entregues.size();

        return new DeliveryEntregasResumoResponse(

                tempoMedioEntrega,

                (long) entregues.size(),

                pedidosEmEntrega,

                pedidosAtrasados

        );
    }
}



