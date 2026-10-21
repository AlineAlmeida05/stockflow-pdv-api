package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.VendaRequest;
import br.com.stockflow.stockflow_api.dto.response.VendaResponse;
import br.com.stockflow.stockflow_api.entity.Venda;
import br.com.stockflow.stockflow_api.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import br.com.stockflow.stockflow_api.dto.request.CancelarVendaRequest;
import java.util.UUID;
import br.com.stockflow.stockflow_api.dto.response.VendaDetalhesResponse;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {

        this.vendaService = vendaService;
    }

    @PostMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA')"
    )
    public Venda salvar(
            @Valid
            @RequestBody VendaRequest request) {

        return vendaService.salvar(request);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public void cancelarVenda(@PathVariable UUID id,
                              @Valid
                              @RequestBody CancelarVendaRequest request) {

        vendaService.cancelarVenda(id, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public VendaDetalhesResponse buscarPorId(@PathVariable UUID id) {

        return vendaService.buscarPorId(id);
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public List<VendaResponse> listar() {

        return vendaService.listar();
    }

    @GetMapping("/relatorio")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public List<VendaDetalhesResponse> listarRelatorio() {

        return vendaService.listarRelatorio();

    }


}