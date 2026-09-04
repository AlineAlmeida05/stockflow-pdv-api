package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Empresa;
import br.com.stockflow.stockflow_api.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Empresa salvar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }
}