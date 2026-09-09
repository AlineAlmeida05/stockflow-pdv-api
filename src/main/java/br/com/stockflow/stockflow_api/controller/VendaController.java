package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.VendaRequest;
import br.com.stockflow.stockflow_api.dto.VendaResponse;

import br.com.stockflow.stockflow_api.entity.Venda;

import br.com.stockflow.stockflow_api.service.VendaService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.com.stockflow.stockflow_api.dto.CancelarVendaRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(
            VendaService vendaService) {

        this.vendaService = vendaService;
    }

    @PostMapping
    public Venda salvar(
            @RequestBody VendaRequest request) {

        return vendaService.salvar(
                request);
    }

    @PostMapping("/{id}/cancelar")
    public void cancelarVenda(
            @PathVariable UUID id,

            @RequestBody CancelarVendaRequest request) {

        vendaService.cancelarVenda(
                id,
                request);
    }

    @GetMapping
    public List<VendaResponse> listar() {

        return vendaService.listar();
    }

}