package br.controller;

import br.dto.response.*;
import br.entity.StatusPedidoDelivery;
import br.service.PedidoDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import br.dto.request.CriarPedidoDeliveryRequest;
import br.entity.PedidoDelivery;
import br.dto.request.AtualizarStatusPedidoDeliveryRequest;
import java.util.UUID;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final PedidoDeliveryService pedidoDeliveryService;

    @GetMapping("/cardapio/{slug}")
    public List<CardapioProdutoResponse> listarCardapio(
            @PathVariable String slug
    ) {

        return pedidoDeliveryService.listarCardapio(
                slug
        );

    }

    @PostMapping("/cardapio/{slug}/pedido")
    public PedidoDeliveryResponse criarPedido(
            @PathVariable String slug,

            @RequestBody
            CriarPedidoDeliveryRequest request
    ) {

        return pedidoDeliveryService.criarPedido(
                slug,
                request
        );

    }

    @GetMapping("/pedidos/{slug}")
    public List<PedidoDeliveryResumoResponse> listarPedidos(

            @PathVariable
            String slug,

            @RequestParam(required = false)
            StatusPedidoDelivery status,

            @RequestParam(
                    required = false,
                    defaultValue = "hoje"
            )
            String periodo

    ) {

        return pedidoDeliveryService.listarPedidos(
                slug,
                status,
                periodo
        );

    }

    @PatchMapping("/pedidos/{slug}/{pedidoId}/status")
    public PedidoDeliveryResponse atualizarStatus(
            @PathVariable String slug,

            @PathVariable UUID pedidoId,

            @RequestBody
            AtualizarStatusPedidoDeliveryRequest request
    ) {

        return pedidoDeliveryService.atualizarStatus(
                slug,
                pedidoId,
                request.status()
        );

    }

    @GetMapping("/pedidos/{slug}/{pedidoId}")
    public PedidoDeliveryDetalheResponse buscarPedido(
            @PathVariable String slug,
            @PathVariable UUID pedidoId
    ) {

        return pedidoDeliveryService.buscarPedido(
                slug,
                pedidoId
        );

    }

    @GetMapping("/resumo/{slug}")
    public PedidoDeliveryResumoDashboardResponse obterResumo(

            @PathVariable
            String slug,

            @RequestParam(
                    required = false,
                    defaultValue = "hoje"
            )
            String periodo

    ) {

        return pedidoDeliveryService.obterResumo(
                slug,
                periodo
        );

    }

    @GetMapping(
            "/formas-pagamento/{slug}"
    )
    public List<FormaPagamentoDeliveryResponse>
    obterFormasPagamento(

            @PathVariable
            String slug,

            @RequestParam(
                    required = false,
                    defaultValue = "hoje"
            )
            String periodo

    ) {

        return pedidoDeliveryService
                .obterFormasPagamento(
                        slug,
                        periodo
                );

    }

    @GetMapping(
            "/entregas/{slug}"
    )
    public DeliveryEntregasResumoResponse
    obterResumoEntregas(

            @PathVariable
            String slug,

            @RequestParam(
                    required = false,
                    defaultValue = "hoje"
            )
            String periodo

    ) {

        return pedidoDeliveryService
                .obterResumoEntregas(
                        slug,
                        periodo
                );

    }

}