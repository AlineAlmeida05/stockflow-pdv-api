package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.VendaRequest;
import br.com.stockflow.stockflow_api.dto.response.VendaResponse;

import br.com.stockflow.stockflow_api.entity.Venda;

import br.com.stockflow.stockflow_api.service.VendaService;

import jakarta.validation.Valid;
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
    public Venda salvar(
            @Valid
            @RequestBody VendaRequest request) {

        return vendaService.salvar(request);
    }

    @PostMapping("/{id}/cancelar")
    public void cancelarVenda(@PathVariable UUID id,
                              @Valid
                              @RequestBody CancelarVendaRequest request) {

        vendaService.cancelarVenda(id, request);
    }

    @GetMapping("/{id}")
    public VendaDetalhesResponse buscarPorId(@PathVariable UUID id) {

        return vendaService.buscarPorId(id);
    }

    @GetMapping
    public List<VendaResponse> listar() {

        return vendaService.listar();
    }

}