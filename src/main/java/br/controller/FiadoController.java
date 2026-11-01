package br.controller;

import br.dto.request.FiadoRequest;
import br.dto.response.FiadoResponse;
import br.entity.Fiado;
import br.service.FiadoService;
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