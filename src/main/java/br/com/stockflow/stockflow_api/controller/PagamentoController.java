package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.PagamentoRequest;
import br.com.stockflow.stockflow_api.dto.PagamentoResponse;
import br.com.stockflow.stockflow_api.entity.Pagamento;
import br.com.stockflow.stockflow_api.service.PagamentoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(
            PagamentoService pagamentoService) {

        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public Pagamento salvar(
            @RequestBody PagamentoRequest request) {

        return pagamentoService.salvar(
                request);
    }

    @GetMapping
    public List<PagamentoResponse> listar() {

        return pagamentoService.listar();
    }
}