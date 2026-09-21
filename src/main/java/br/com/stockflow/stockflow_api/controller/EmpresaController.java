package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import br.com.stockflow.stockflow_api.dto.response.EmpresaResponse;
import br.com.stockflow.stockflow_api.dto.EmpresaUpdateRequest;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {

        this.empresaService = empresaService;
    }

    @GetMapping
    public EmpresaResponse obter() {

        return empresaService
                .obterEmpresa();
    }

    @PutMapping
    public EmpresaResponse atualizar(
            @Valid
            @RequestBody EmpresaUpdateRequest request

    ) {

        return empresaService
                .atualizarEmpresa(request);
    }

}