package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.service.TenantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")

public class TenantController {

    private final TenantService tenantService;

    public TenantController(
            TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public List<Tenant> listar() {
        return tenantService.listarTodos();
    }

    @PostMapping
    public Tenant salvar(
            @RequestBody Tenant tenant) {
        return tenantService.salvar(tenant);
    }

    @PutMapping("/{id}")
    public Tenant atualizar(
            @PathVariable UUID id,
            @RequestBody Tenant tenant) {

        return tenantService.atualizar(
                id,
                tenant);

    }

    @DeleteMapping("/{id}")
    public void excluir(
            @PathVariable UUID id) {
        tenantService.excluir(id);
    }

    @GetMapping("/slug/{slug}")
    public Tenant buscarPorSlug(
            @PathVariable String slug) {

        return tenantService.buscarPorSlug(
                slug);

    }

    @GetMapping("/teste")
    public String teste() {

        return "OKOKOK";

    }
}
