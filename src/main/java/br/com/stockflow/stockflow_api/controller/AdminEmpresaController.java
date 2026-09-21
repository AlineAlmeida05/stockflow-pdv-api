package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.entity.Empresa;
import br.com.stockflow.stockflow_api.service.EmpresaService;

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
    public List<Empresa> listar() {

        return empresaService.listarTodas();

    }

    @PostMapping
    public Empresa salvar(
            @RequestBody Empresa empresa
    ) {

        return empresaService.salvar(
                empresa
        );

    }
}