package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.FiadoRequest;
import br.com.stockflow.stockflow_api.dto.response.FiadoResponse;
import br.com.stockflow.stockflow_api.entity.Fiado;
import br.com.stockflow.stockflow_api.service.FiadoService;

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
    public Fiado salvar(
            @RequestBody FiadoRequest request) {

        return fiadoService.salvar(
                request);
    }

    @GetMapping
    public List<FiadoResponse> listar() {

        return fiadoService.listar();
    }
}