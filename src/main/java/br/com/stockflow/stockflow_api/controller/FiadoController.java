package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.FiadoRequest;
import br.com.stockflow.stockflow_api.dto.response.FiadoResponse;
import br.com.stockflow.stockflow_api.entity.Fiado;
import br.com.stockflow.stockflow_api.service.FiadoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fiados")
public class FiadoController {

    private final FiadoService fiadoService;

    public FiadoController(
            FiadoService fiadoService) {

        this.fiadoService = fiadoService;
    }

    @PostMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA')"
    )
    public Fiado salvar(
            @Valid
            @RequestBody FiadoRequest request) {

        return fiadoService.salvar(
                request);
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA')"
    )
    public List<FiadoResponse> listar() {

        return fiadoService.listar();
    }
}