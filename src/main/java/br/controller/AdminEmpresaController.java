package br.controller;

import br.dto.request.EmpresaCreateRequest;
import br.dto.response.EmpresaResponse;
import br.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<EmpresaResponse> listar() {

        return empresaService.listarTodas();

    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public EmpresaResponse salvar(

            @Valid
            @RequestBody
            EmpresaCreateRequest request

    ) {
        return empresaService.salvar(request);
    }
}