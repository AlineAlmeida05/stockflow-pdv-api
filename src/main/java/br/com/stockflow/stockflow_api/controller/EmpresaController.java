package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.entity.Empresa;
import br.com.stockflow.stockflow_api.service.EmpresaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public List<Empresa> listar() {
        return empresaService.listarTodas();
    }

    @PostMapping
    public Empresa salvar(@RequestBody Empresa empresa) {
        return empresaService.salvar(empresa);
    }
}