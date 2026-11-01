package br.controller;

import br.dto.response.AuditoriaResponse;
import br.service.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(
            AuditoriaService auditoriaService
    ) {

        this.auditoriaService = auditoriaService;

    }

    @GetMapping
    public ResponseEntity<List<AuditoriaResponse>>
    listar() {

        return ResponseEntity.ok(
                auditoriaService.listar()
        );

    }

}