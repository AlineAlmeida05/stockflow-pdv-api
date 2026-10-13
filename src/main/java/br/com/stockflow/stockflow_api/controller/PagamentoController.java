package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.PagamentoRequest;
import br.com.stockflow.stockflow_api.dto.response.PagamentoResponse;
import br.com.stockflow.stockflow_api.entity.Pagamento;
import br.com.stockflow.stockflow_api.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA')"
    )
    public Pagamento salvar(
            @Valid
            @RequestBody PagamentoRequest request) {

        return pagamentoService.salvar(
                request);
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA')"
    )
    public List<PagamentoResponse> listar() {

        return pagamentoService.listar();
    }
}