package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.response.AuditoriaResponse;
import br.com.stockflow.stockflow_api.service.AuditoriaService;
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

        this.auditoriaService =
                auditoriaService;

    }

    @GetMapping
    public ResponseEntity<List<AuditoriaResponse>>
    listar() {

        return ResponseEntity.ok(
                auditoriaService.listar()
        );

    }

}