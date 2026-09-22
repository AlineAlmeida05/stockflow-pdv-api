package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.EmpresaCreateRequest;
import br.com.stockflow.stockflow_api.dto.response.EmpresaResponse;
import br.com.stockflow.stockflow_api.entity.Empresa;
import br.com.stockflow.stockflow_api.service.EmpresaService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/empresas")
public class AdminEmpresaController {

    private final EmpresaService empresaService;

    public AdminEmpresaController(
            EmpresaService empresaService
    ) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public List<EmpresaResponse> listar() {

        return empresaService.listarTodas();

    }

    @PostMapping
    public EmpresaResponse salvar(

            @Valid
            @RequestBody
            EmpresaCreateRequest request

    ) {
        return empresaService.salvar(request);
    }
}