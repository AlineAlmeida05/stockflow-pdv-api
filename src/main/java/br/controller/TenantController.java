package br.controller;

import br.dto.request.TenantRequest;
import br.dto.response.TenantResponse;
import br.entity.Tenant;
import br.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/api/tenants")

public class TenantController {

    private final TenantService tenantService;

    public TenantController(
            TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<Tenant> listar() {
        return tenantService.listarTodos();
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public TenantResponse salvar(
            @Valid @RequestBody TenantRequest request) {

        return tenantService.salvar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public TenantResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody TenantRequest request) {

        return tenantService.atualizar(
                id,
                request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void excluir(
            @PathVariable UUID id) {
        tenantService.excluir(id);
    }

    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Tenant buscarPorSlug(
            @PathVariable String slug) {

        return tenantService.buscarPorSlug(
                slug);

    }
}
