package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}