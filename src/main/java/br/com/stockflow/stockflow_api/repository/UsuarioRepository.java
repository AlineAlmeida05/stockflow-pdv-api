package br.com.stockflow.stockflow_api.repository;

import br.com.stockflow.stockflow_api.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository
                extends JpaRepository<Usuario, UUID> {

        Optional<Usuario> findByEmail(
                        String email);

        List<Usuario> findByTenantId(
                        UUID tenantId);

}