package br.controller;

import br.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.dto.response.EmpresaResponse;
import br.dto.request.EmpresaUpdateRequest;

@RestController
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/api/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {

        this.empresaService = empresaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public EmpresaResponse obter() {

        return empresaService
                .obterEmpresa();
    }

    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public EmpresaResponse atualizar(
            @Valid
            @RequestBody EmpresaUpdateRequest request

    ) {

        return empresaService
                .atualizarEmpresa(request);
    }

}